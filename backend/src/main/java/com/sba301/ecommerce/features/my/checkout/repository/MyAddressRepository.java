package com.sba301.ecommerce.features.my.checkout.repository;

import com.sba301.ecommerce.features.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MyAddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByUserIdOrderByIsDefaultDesc(Long userId);
}
