package com.sba301.ecommerce.features.my.order.service;

import com.sba301.ecommerce.exception.BadRequestException;
import com.sba301.ecommerce.features.auth.repositories.UserRepository;
import com.sba301.ecommerce.features.entities.*;
import com.sba301.ecommerce.features.entities.enums.OrderChannel;
import com.sba301.ecommerce.features.entities.enums.OrderPaymentStatus;
import com.sba301.ecommerce.features.entities.enums.OrderStatus;
import com.sba301.ecommerce.features.entities.enums.PaymentMethod;
import com.sba301.ecommerce.features.my.cart.repository.MyProductVariantRepository;
import com.sba301.ecommerce.features.my.checkout.repository.MyAddressRepository;
import com.sba301.ecommerce.features.my.checkout.repository.ShippingMethodRepository;
import com.sba301.ecommerce.features.my.checkout.repository.VoucherRepository;
import com.sba301.ecommerce.features.my.order.dto.CreateMyOrderRequest;
import com.sba301.ecommerce.features.my.order.dto.MyOrderResponse;
import com.sba301.ecommerce.features.my.order.dto.OrderItemResponse;
import com.sba301.ecommerce.features.my.order.repository.MyOrderRepository;
import com.sba301.ecommerce.features.my.payment.VnpayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyOrderServiceImpl implements MyOrderService {

    private final MyOrderRepository orderRepository;
    private final UserRepository userRepository;
    private final MyProductVariantRepository variantRepository;
    private final MyAddressRepository addressRepository;
    private final ShippingMethodRepository shippingMethodRepository;
    private final VoucherRepository voucherRepository;
    private final VnpayService vnpayService;

    // ===== TẠO ĐƠN HÀNG =====

    @Override
    @Transactional
    public MyOrderResponse createOrder(Long userId, CreateMyOrderRequest request, HttpServletRequest httpRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("Người dùng không tồn tại."));

        // 1. Lấy địa chỉ giao hàng
        Address shippingAddress = null;
        if (request.getShippingAddressId() != null) {
            shippingAddress = addressRepository.findById(request.getShippingAddressId())
                    .orElseThrow(() -> new BadRequestException("Địa chỉ giao hàng không tồn tại."));
        }

        // 2. Lấy phương thức vận chuyển
        BigDecimal shippingFee = BigDecimal.ZERO;
        if (request.getShippingMethodId() != null) {
            ShippingMethod method = shippingMethodRepository.findById(request.getShippingMethodId())
                    .orElse(null);
            if (method != null) {
                shippingFee = method.getFee();
            }
        }

        // 3. Tạo order items và tính subtotal
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (CreateMyOrderRequest.OrderItemRequest itemReq : request.getItems()) {
            ProductVariant variant = variantRepository.findByIdWithProductAndImages(itemReq.getVariantId())
                    .orElseThrow(() -> new BadRequestException(
                            "Sản phẩm với variantId=" + itemReq.getVariantId() + " không tồn tại."));

            if (!variant.getIsActive()) {
                throw new BadRequestException("Sản phẩm " + variant.getSku() + " đã ngừng kinh doanh.");
            }
            if (variant.getStockQuantity() < itemReq.getQuantity()) {
                throw new BadRequestException("Sản phẩm " + variant.getSku()
                        + " không đủ tồn kho (còn " + variant.getStockQuantity() + " sản phẩm).");
            }

            // Trừ tồn kho (optimistic locking qua @Version)
            variant.setStockQuantity(variant.getStockQuantity() - itemReq.getQuantity());

            BigDecimal lineSubtotal = variant.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            subtotal = subtotal.add(lineSubtotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setVariant(variant);
            orderItem.setProductName(variant.getProduct().getName());
            orderItem.setVariantInfo(variant.getSize() + " / " + variant.getColor());
            orderItem.setUnitPrice(variant.getPrice());
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setSubtotal(lineSubtotal);
            orderItems.add(orderItem);
        }

        // 4. Tính discount từ voucher (nếu có)
        BigDecimal discount = BigDecimal.ZERO;
        if (request.getVoucherCode() != null && !request.getVoucherCode().isBlank()) {
            var voucherOpt = voucherRepository.findByCodeAndIsActiveTrue(request.getVoucherCode().toUpperCase());
            if (voucherOpt.isPresent()) {
                Voucher voucher = voucherOpt.get();
                discount = calculateDiscount(voucher, subtotal, shippingFee);
            }
        }

        BigDecimal totalAmount = subtotal.add(shippingFee).subtract(discount).max(BigDecimal.ZERO);

        // 5. Tạo Order entity
        String orderCode = generateOrderCode();

        Order order = new Order();
        order.setOrderCode(orderCode);
        order.setUser(user);
        order.setChannel(OrderChannel.ONLINE);
        order.setShippingAddress(shippingAddress);
        order.setSubtotal(subtotal);
        order.setShippingFee(shippingFee);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(OrderPaymentStatus.UNPAID);
        order.setNote(request.getNote());

        Order savedOrder = orderRepository.save(order);

        // Gắn order vào từng item và save
        for (OrderItem item : orderItems) {
            item.setOrder(savedOrder);
        }
        savedOrder.getItems().addAll(orderItems);
        orderRepository.save(savedOrder);

        // 6. Tạo payment URL nếu cần
        String paymentUrl = null;
        String paymentMethodStr = request.getPaymentMethod();

        if ("VNPAY".equalsIgnoreCase(paymentMethodStr)) {
            String clientIp = vnpayService.getClientIp(httpRequest);
            paymentUrl = vnpayService.createPaymentUrl(orderCode, totalAmount, clientIp);
        }

        return toOrderResponse(savedOrder, paymentUrl);
    }

    // ===== LẤY ĐƠN HÀNG CỦA USER =====

    @Override
    public List<MyOrderResponse> getMyOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(o -> toOrderResponse(o, null))
                .collect(Collectors.toList());
    }

    // ===== LẤY CHI TIẾT ĐƠN HÀNG =====

    @Override
    public MyOrderResponse getOrderByCode(Long userId, String orderCode) {
        Order order = orderRepository.findByOrderCodeWithItems(orderCode)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy đơn hàng " + orderCode));

        if (!order.getUser().getId().equals(userId)) {
            throw new BadRequestException("Bạn không có quyền xem đơn hàng này.");
        }

        return toOrderResponse(order, null);
    }

    // ===== HELPERS =====

    private String generateOrderCode() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = orderRepository.count() + 1;
        return "ORD-" + date + "-" + String.format("%04d", count);
    }

    private BigDecimal calculateDiscount(Voucher voucher, BigDecimal subtotal, BigDecimal shippingFee) {
        return switch (voucher.getType().toUpperCase()) {
            case "FIXED" -> voucher.getAmount().min(subtotal);
            case "PERCENT" -> {
                BigDecimal d = subtotal.multiply(voucher.getAmount()).divide(BigDecimal.valueOf(100));
                if (voucher.getMaxDiscount() != null) {
                    d = d.min(voucher.getMaxDiscount());
                }
                yield d;
            }
            case "SHIPPING" -> {
                BigDecimal d = voucher.getAmount().min(shippingFee);
                if (voucher.getMaxDiscount() != null) {
                    d = d.min(voucher.getMaxDiscount());
                }
                yield d;
            }
            default -> BigDecimal.ZERO;
        };
    }

    private MyOrderResponse toOrderResponse(Order order, String paymentUrl) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getProductName(),
                        item.getVariantInfo(),
                        item.getUnitPrice(),
                        item.getQuantity(),
                        item.getSubtotal()
                ))
                .collect(Collectors.toList());

        return new MyOrderResponse(
                order.getId(),
                order.getOrderCode(),
                order.getStatus().name(),
                order.getPaymentStatus().name(),
                order.getChannel().name(),
                order.getSubtotal(),
                order.getShippingFee(),
                order.getTotalAmount(),
                order.getNote(),
                itemResponses,
                paymentUrl
        );
    }
}
