package com.sba301.ecommerce.config;

import com.sba301.ecommerce.features.entities.*;
import com.sba301.ecommerce.features.entities.enums.*;
import com.sba301.ecommerce.features.my.checkout.repository.ShippingMethodRepository;
import com.sba301.ecommerce.features.my.checkout.repository.VoucherRepository;
import com.sba301.ecommerce.features.auth.repositories.UserRepository;
import com.sba301.ecommerce.features.category.repository.CategoryRepository;
import com.sba301.ecommerce.features.product.repository.ProductRepository;
import com.sba301.ecommerce.features.my.cart.repository.MyCartRepository;
import com.sba301.ecommerce.features.my.cart.repository.MyCartItemRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Seed dữ liệu mẫu khi khởi động lần đầu.
 * Chỉ insert nếu bảng còn trống để tránh trùng lặp.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    private final ShippingMethodRepository shippingMethodRepository;
    private final VoucherRepository voucherRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final MyCartRepository cartRepository;
    private final MyCartItemRepository cartItemRepository;

    @Bean
    public CommandLineRunner seedData() {
        return args -> {
            User user = seedUser();
            seedShippingMethods();
            seedVouchers();
            seedProductsAndCart(user);
        };
    }

    private void seedShippingMethods() {
        if (shippingMethodRepository.count() > 0) {
            log.info("[DataSeeder] Bảng shipping_methods đã có dữ liệu, bỏ qua seed.");
            return;
        }

        ShippingMethod standard = new ShippingMethod();
        standard.setName("Giao tiêu chuẩn");
        standard.setEta("2-4 ngày");
        standard.setFee(new BigDecimal("25000"));
        standard.setDescription("Phù hợp đơn hàng thông thường trong nội thành.");
        standard.setIsActive(true);

        ShippingMethod express = new ShippingMethod();
        express.setName("Giao nhanh");
        express.setEta("Trong ngày (nội thành)");
        express.setFee(new BigDecimal("45000"));
        express.setDescription("Ưu tiên kho và đối tác vận chuyển nhanh.");
        express.setIsActive(true);

        shippingMethodRepository.save(standard);
        shippingMethodRepository.save(express);
        log.info("[DataSeeder] Đã seed 2 phương thức vận chuyển.");
    }

    private void seedVouchers() {
        if (voucherRepository.count() > 0) {
            log.info("[DataSeeder] Bảng vouchers đã có dữ liệu, bỏ qua seed.");
            return;
        }

        Voucher freeship = new Voucher();
        freeship.setCode("FREESHIP30");
        freeship.setType("SHIPPING");
        freeship.setAmount(new BigDecimal("30000"));
        freeship.setDescription("Giảm tối đa 30.000VND phí vận chuyển.");
        freeship.setIsActive(true);

        Voucher sale40k = new Voucher();
        sale40k.setCode("SALE40K");
        sale40k.setType("FIXED");
        sale40k.setAmount(new BigDecimal("40000"));
        sale40k.setDescription("Giảm trực tiếp 40.000VND vào tổng tiền hàng.");
        sale40k.setIsActive(true);

        Voucher save10 = new Voucher();
        save10.setCode("SAVE10");
        save10.setType("PERCENT");
        save10.setAmount(new BigDecimal("10"));
        save10.setMaxDiscount(new BigDecimal("120000"));
        save10.setDescription("Giảm 10% tối đa 120.000VND.");
        save10.setIsActive(true);

        voucherRepository.save(freeship);
        voucherRepository.save(sale40k);
        voucherRepository.save(save10);
        log.info("[DataSeeder] Đã seed 3 voucher mẫu: FREESHIP30, SALE40K, SAVE10.");
    }

    private User seedUser() {
        if (userRepository.existsUserByEmail("customer@gmail.com")) {
            log.info("[DataSeeder] User customer@gmail.com đã tồn tại, bỏ qua seed.");
            return userRepository.findUserByEmail("customer@gmail.com");
        }

        User user = new User();
        user.setEmail("customer@gmail.com");
        user.setPasswordHash(passwordEncoder.encode("password"));
        user.setFullName("Nguyễn Văn A");
        user.setPhone("0987654321");
        user.setRole(com.sba301.ecommerce.features.entities.enums.Role.CUSTOMER);
        user.setEmailVerified(true);
        user.setFailedLoginAttempts(0);
        user.setStatus("ACTIVE");

        User savedUser = userRepository.save(user);
        log.info("[DataSeeder] Đã seed tài khoản test: customer@gmail.com / password");
        return savedUser;
    }

    private void seedProductsAndCart(User user) {
        if (productRepository.count() > 0) {
            log.info("[DataSeeder] Đã có sản phẩm trong DB, bỏ qua seed products.");
            return;
        }

        // 1. Create Category
        Category category = new Category();
        category.setName("Thời trang Nam");
        category.setSlug("thoi-trang-nam");
        category.setDisplayOrder(1);
        category.setIsActive(true);
        category = categoryRepository.save(category);

        // 2. Create Product 1 (Áo Khoác)
        Product p1 = new Product();
        p1.setCategory(category);
        p1.setName("Áo Khoác Nam Mùa Đông");
        p1.setSlug("ao-khoac-nam-mua-dong");
        p1.setBrand("Antigravity");
        p1.setBasePrice(new BigDecimal("550000"));
        p1.setDescription("Áo khoác ấm áp cho mùa đông lạnh giá.");
        p1.setStatus(ProductStatus.ACTIVE);
        p1.setVariants(new ArrayList<>());
        p1.setImages(new ArrayList<>());

        // Variant for Product 1
        ProductVariant v1 = new ProductVariant();
        v1.setProduct(p1);
        v1.setSku("AOK-NAM-L-DEN");
        v1.setSize("L");
        v1.setColor("Đen");
        v1.setPrice(new BigDecimal("550000"));
        v1.setStockQuantity(10);
        v1.setIsActive(true);
        p1.getVariants().add(v1);

        // Image for Product 1
        ProductImage img1 = new ProductImage();
        img1.setProduct(p1);
        img1.setUrl("https://images.unsplash.com/photo-1556821840-3a63f95609a7?auto=format&fit=crop&w=640&q=80");
        img1.setDisplayOrder(0);
        img1.setIsPrimary(true);
        p1.getImages().add(img1);

        p1 = productRepository.save(p1);

        // 3. Create Product 2 (Giày)
        Product p2 = new Product();
        p2.setCategory(category);
        p2.setName("Giày Thể Thao Sneaker");
        p2.setSlug("giay-the-thao-sneaker");
        p2.setBrand("Antigravity");
        p2.setBasePrice(new BigDecimal("850000"));
        p2.setDescription("Giày sneaker phong cách năng động.");
        p2.setStatus(ProductStatus.ACTIVE);
        p2.setVariants(new ArrayList<>());
        p2.setImages(new ArrayList<>());

        // Variant for Product 2
        ProductVariant v2 = new ProductVariant();
        v2.setProduct(p2);
        v2.setSku("GIAY-SNE-42-TRG");
        v2.setSize("42");
        v2.setColor("Trắng");
        v2.setPrice(new BigDecimal("850000"));
        v2.setStockQuantity(5);
        v2.setIsActive(true);
        p2.getVariants().add(v2);

        // Image for Product 2
        ProductImage img2 = new ProductImage();
        img2.setProduct(p2);
        img2.setUrl("https://images.unsplash.com/photo-1549298916-b41d501d3772?auto=format&fit=crop&w=640&q=80");
        img2.setDisplayOrder(0);
        img2.setIsPrimary(true);
        p2.getImages().add(img2);

        p2 = productRepository.save(p2);

        // 4. Create Product 3 (Áo Thun)
        Product p3 = new Product();
        p3.setCategory(category);
        p3.setName("Áo Thun Nam Basic");
        p3.setSlug("ao-thun-nam-basic");
        p3.setBrand("Antigravity");
        p3.setBasePrice(new BigDecimal("250000"));
        p3.setDescription("Áo thun basic cotton thoáng mát.");
        p3.setStatus(ProductStatus.ACTIVE);
        p3.setVariants(new ArrayList<>());
        p3.setImages(new ArrayList<>());

        ProductVariant v3 = new ProductVariant();
        v3.setProduct(p3);
        v3.setSku("ATHUN-NAM-M-TRG");
        v3.setSize("M");
        v3.setColor("Trắng");
        v3.setPrice(new BigDecimal("250000"));
        v3.setStockQuantity(15);
        v3.setIsActive(true);
        p3.getVariants().add(v3);

        ProductImage img3 = new ProductImage();
        img3.setProduct(p3);
        img3.setUrl("https://images.unsplash.com/photo-1521572267360-ee0c2909d518?auto=format&fit=crop&w=640&q=80");
        img3.setDisplayOrder(0);
        img3.setIsPrimary(true);
        p3.getImages().add(img3);

        p3 = productRepository.save(p3);

        // 5. Create Cart for User if not exists
        Cart cart = cartRepository.findByUserId(user.getId()).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });

        // Clear existing items in cart to avoid duplicates if database not wiped
        cartItemRepository.deleteAll(cartItemRepository.findAllByCartId(cart.getId()));

        // 6. Add CartItems
        CartItem item1 = new CartItem();
        item1.setCart(cart);
        item1.setVariant(p1.getVariants().get(0));
        item1.setQuantity(2);
        cartItemRepository.save(item1);

        CartItem item2 = new CartItem();
        item2.setCart(cart);
        item2.setVariant(p2.getVariants().get(0));
        item2.setQuantity(1);
        cartItemRepository.save(item2);

        log.info("[DataSeeder] Đã seed danh mục, 3 sản phẩm và nạp giỏ hàng mẫu cho customer@gmail.com.");
    }
}

