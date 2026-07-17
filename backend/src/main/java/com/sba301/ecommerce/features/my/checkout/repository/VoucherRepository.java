package com.sba301.ecommerce.features.my.checkout.repository;

import com.sba301.ecommerce.features.entities.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    Optional<Voucher> findByCodeAndIsActiveTrue(String code);
}
