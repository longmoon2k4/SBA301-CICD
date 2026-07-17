package com.sba301.ecommerce.features.my.cart.repository;

import com.sba301.ecommerce.features.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MyCartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("""
            SELECT ci FROM CartItem ci
            JOIN FETCH ci.variant v
            JOIN FETCH v.product p
            LEFT JOIN FETCH p.images
            WHERE ci.id = :id
            """)
    Optional<CartItem> findByIdWithVariantAndProduct(@Param("id") Long id);

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.variant.id = :variantId")
    Optional<CartItem> findByCartIdAndVariantId(@Param("cartId") Long cartId,
                                                @Param("variantId") Long variantId);

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId")
    List<CartItem> findAllByCartId(@Param("cartId") Long cartId);
}
