package com.sba301.ecommerce.features.my.checkout.service;

import com.sba301.ecommerce.exception.BadRequestException;
import com.sba301.ecommerce.features.auth.repositories.UserRepository;
import com.sba301.ecommerce.features.entities.Address;
import com.sba301.ecommerce.features.entities.ShippingMethod;
import com.sba301.ecommerce.features.entities.User;
import com.sba301.ecommerce.features.entities.Voucher;
import com.sba301.ecommerce.features.my.checkout.dto.*;
import com.sba301.ecommerce.features.my.checkout.repository.MyAddressRepository;
import com.sba301.ecommerce.features.my.checkout.repository.ShippingMethodRepository;
import com.sba301.ecommerce.features.my.checkout.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyCheckoutServiceImpl implements MyCheckoutService {

    private final MyAddressRepository addressRepository;
    private final ShippingMethodRepository shippingMethodRepository;
    private final VoucherRepository voucherRepository;
    private final UserRepository userRepository;

    // ===== ADDRESSES =====

    @Override
    public List<AddressResponse> getAddresses(Long userId) {
        return addressRepository.findByUserIdOrderByIsDefaultDesc(userId)
                .stream()
                .map(this::toAddressResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddressResponse addAddress(Long userId, AddAddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("Người dùng không tồn tại."));

        // Nếu địa chỉ mới là mặc định, hủy mặc định của địa chỉ cũ
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            List<Address> existing = addressRepository.findByUserIdOrderByIsDefaultDesc(userId);
            existing.forEach(a -> a.setIsDefault(false));
            addressRepository.saveAll(existing);
        }

        Address address = new Address();
        address.setUser(user);
        address.setRecipientName(request.getRecipientName());
        address.setPhone(request.getPhone());
        address.setProvince(request.getProvince());
        address.setDistrict(request.getDistrict());
        address.setWard(request.getWard());
        address.setStreet(request.getStreet());
        address.setIsDefault(Boolean.TRUE.equals(request.getIsDefault()));

        Address saved = addressRepository.save(address);
        return toAddressResponse(saved);
    }

    // ===== SHIPPING METHODS =====

    @Override
    public List<ShippingMethodResponse> getShippingMethods() {
        return shippingMethodRepository.findByIsActiveTrueOrderById()
                .stream()
                .map(this::toShippingResponse)
                .collect(Collectors.toList());
    }

    // ===== VOUCHER =====

    @Override
    public VoucherResponse applyVoucher(String code) {
        Voucher voucher = voucherRepository.findByCodeAndIsActiveTrue(code.toUpperCase())
                .orElseThrow(() -> new BadRequestException("Mã giảm giá không hợp lệ hoặc đã hết hạn."));

        // Kiểm tra thời hạn
        LocalDateTime now = LocalDateTime.now();
        if (voucher.getEndAt() != null && now.isAfter(voucher.getEndAt())) {
            throw new BadRequestException("Mã giảm giá đã hết hạn.");
        }
        if (voucher.getStartAt() != null && now.isBefore(voucher.getStartAt())) {
            throw new BadRequestException("Mã giảm giá chưa có hiệu lực.");
        }

        return new VoucherResponse(
                voucher.getCode(),
                voucher.getType(),
                voucher.getAmount(),
                voucher.getMaxDiscount(),
                voucher.getDescription()
        );
    }

    // ===== HELPERS =====

    private AddressResponse toAddressResponse(Address a) {
        return new AddressResponse(
                a.getId(),
                a.getRecipientName(),
                a.getPhone(),
                a.getProvince(),
                a.getDistrict(),
                a.getWard(),
                a.getStreet(),
                a.getIsDefault()
        );
    }

    private ShippingMethodResponse toShippingResponse(ShippingMethod m) {
        return new ShippingMethodResponse(
                m.getId(),
                m.getName(),
                m.getEta(),
                m.getFee(),
                m.getDescription()
        );
    }
}
