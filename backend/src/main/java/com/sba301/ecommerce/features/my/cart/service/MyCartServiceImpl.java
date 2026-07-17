package com.sba301.ecommerce.features.my.cart.service;

import com.sba301.ecommerce.exception.BadRequestException;
import com.sba301.ecommerce.features.entities.Cart;
import com.sba301.ecommerce.features.entities.CartItem;
import com.sba301.ecommerce.features.entities.ProductImage;
import com.sba301.ecommerce.features.entities.ProductVariant;
import com.sba301.ecommerce.features.entities.User;
import com.sba301.ecommerce.features.auth.repositories.UserRepository;
import com.sba301.ecommerce.features.my.cart.dto.*;
import com.sba301.ecommerce.features.my.cart.repository.MyCartItemRepository;
import com.sba301.ecommerce.features.my.cart.repository.MyCartRepository;
import com.sba301.ecommerce.features.my.cart.repository.MyProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyCartServiceImpl implements MyCartService {

    private final MyCartRepository cartRepository;
    private final MyCartItemRepository cartItemRepository;
    private final MyProductVariantRepository variantRepository;
    private final UserRepository userRepository;

    // ===== GET MY CART =====

    @Override
    @Transactional
    public MyCartResponse getMyCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        // Re-fetch với items để tránh lazy loading issue
        cart = cartRepository.findByUserIdWithItems(userId).orElse(cart);
        return toCartResponse(cart);
    }

    // ===== ADD ITEM =====

    @Override
    @Transactional
    public MyCartItemResponse addItem(Long userId, AddCartItemRequest request) {
        ProductVariant variant = variantRepository.findByIdWithProductAndImages(request.getVariantId())
                .orElseThrow(() -> new BadRequestException("Sản phẩm không tồn tại."));

        if (!variant.getIsActive() || variant.getStockQuantity() <= 0) {
            throw new BadRequestException("Sản phẩm " + variant.getSku() + " đã hết hàng hoặc ngừng kinh doanh.");
        }

        Cart cart = getOrCreateCart(userId);

        Optional<CartItem> existingOpt = cartItemRepository.findByCartIdAndVariantId(cart.getId(), variant.getId());

        CartItem cartItem;
        if (existingOpt.isPresent()) {
            cartItem = existingOpt.get();
            int newQty = Math.min(cartItem.getQuantity() + request.getQuantity(), variant.getStockQuantity());
            cartItem.setQuantity(newQty);
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setVariant(variant);
            cartItem.setQuantity(Math.min(request.getQuantity(), variant.getStockQuantity()));
        }

        cartItemRepository.save(cartItem);
        return toItemResponse(cartItem, variant);
    }

    // ===== UPDATE QUANTITY =====

    @Override
    @Transactional
    public MyCartItemResponse updateItemQuantity(Long userId, Long itemId, UpdateCartItemRequest request) {
        CartItem cartItem = cartItemRepository.findByIdWithVariantAndProduct(itemId)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy sản phẩm trong giỏ hàng."));

        // Kiểm tra ownership
        if (!cartItem.getCart().getUser().getId().equals(userId)) {
            throw new BadRequestException("Không có quyền chỉnh sửa sản phẩm này.");
        }

        ProductVariant variant = cartItem.getVariant();
        int bounded = Math.max(1, Math.min(request.getQuantity(), variant.getStockQuantity()));
        cartItem.setQuantity(bounded);
        cartItemRepository.save(cartItem);

        return toItemResponse(cartItem, variant);
    }

    // ===== REMOVE ITEM =====

    @Override
    @Transactional
    public void removeItem(Long userId, Long itemId) {
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy sản phẩm trong giỏ hàng."));

        if (!cartItem.getCart().getUser().getId().equals(userId)) {
            throw new BadRequestException("Không có quyền xóa sản phẩm này.");
        }

        cartItemRepository.delete(cartItem);
    }

    // ===== REMOVE UNAVAILABLE ITEMS =====

    @Override
    @Transactional
    public void removeUnavailableItems(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        if (cart == null) return;

        List<CartItem> allItems = cartItemRepository.findAllByCartId(cart.getId());
        List<CartItem> toRemove = allItems.stream()
                .filter(item -> !item.getVariant().getIsActive() || item.getVariant().getStockQuantity() <= 0)
                .collect(Collectors.toList());

        cartItemRepository.deleteAll(toRemove);
    }

    // ===== REMOVE ITEMS BY IDS (sau khi đặt hàng) =====

    @Override
    @Transactional
    public void removeItemsByIds(Long userId, List<Long> itemIds) {
        if (itemIds == null || itemIds.isEmpty()) return;

        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        if (cart == null) return;

        List<CartItem> toRemove = cartItemRepository.findAllById(itemIds).stream()
                .filter(item -> item.getCart().getId().equals(cart.getId()))
                .collect(Collectors.toList());

        cartItemRepository.deleteAll(toRemove);
    }

    // ===== HELPERS =====

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BadRequestException("Người dùng không tồn tại."));
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });
    }

    private MyCartResponse toCartResponse(Cart cart) {
        List<MyCartItemResponse> itemResponses = cart.getItems().stream()
                .map(item -> toItemResponse(item, item.getVariant()))
                .collect(Collectors.toList());

        return new MyCartResponse(
                cart.getId(),
                cart.getUser().getId(),
                "ONLINE",
                itemResponses
        );
    }

    private MyCartItemResponse toItemResponse(CartItem item, ProductVariant variant) {
        String thumbnail = variant.getProduct().getImages().stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
                .map(ProductImage::getUrl)
                .findFirst()
                .orElse(variant.getProduct().getImages().isEmpty()
                        ? null
                        : variant.getProduct().getImages().get(0).getUrl());

        MyCartItemResponse res = new MyCartItemResponse();
        res.setId(item.getId());
        res.setCartItemId(item.getId());
        res.setVariantId(variant.getId());
        res.setProductId(variant.getProduct().getId());
        res.setProductName(variant.getProduct().getName());
        res.setSku(variant.getSku());
        res.setSize(variant.getSize());
        res.setColor(variant.getColor());
        res.setUnitPrice(variant.getPrice());
        res.setQuantity(item.getQuantity());
        res.setStockQuantity(variant.getStockQuantity());
        res.setIsActive(variant.getIsActive());
        res.setThumbnail(thumbnail);
        return res;
    }
}
