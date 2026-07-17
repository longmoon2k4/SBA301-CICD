package com.sba301.ecommerce.features.my.cart.repository;

import com.sba301.ecommerce.features.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MyCartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserId(Long userId);

    /**
     * Fetch cart cùng items, variant, product và images trong 1 query để tránh N+1.
     * Dùng LEFT JOIN FETCH images để lấy thumbnail (ảnh primary).
     */
    @Query("""
            SELECT DISTINCT c FROM Cart c
            LEFT JOIN FETCH c.items ci
            LEFT JOIN FETCH ci.variant v
            LEFT JOIN FETCH v.product p
            WHERE c.user.id = :userId
            """)
    Optional<Cart> findByUserIdWithItems(@Param("userId") Long userId);
}
