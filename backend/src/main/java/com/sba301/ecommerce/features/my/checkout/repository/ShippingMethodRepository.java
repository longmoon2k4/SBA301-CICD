package com.sba301.ecommerce.features.my.checkout.repository;

import com.sba301.ecommerce.features.entities.ShippingMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShippingMethodRepository extends JpaRepository<ShippingMethod, Long> {

    List<ShippingMethod> findByIsActiveTrueOrderById();
}
