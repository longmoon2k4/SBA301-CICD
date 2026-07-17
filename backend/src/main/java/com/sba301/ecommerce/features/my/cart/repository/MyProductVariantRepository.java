package com.sba301.ecommerce.features.my.cart.repository;

import com.sba301.ecommerce.features.entities.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MyProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    @Query("""
            SELECT v FROM ProductVariant v
            JOIN FETCH v.product p
            LEFT JOIN FETCH p.images
            WHERE v.id = :id
            """)
    Optional<ProductVariant> findByIdWithProductAndImages(@Param("id") Long id);
}
