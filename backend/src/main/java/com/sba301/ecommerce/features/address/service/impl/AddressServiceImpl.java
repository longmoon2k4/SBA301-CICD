package com.sba301.ecommerce.features.address.service.impl;

import com.sba301.ecommerce.features.address.repository.AddressRepository;
import com.sba301.ecommerce.features.address.service.AddressService;
import com.sba301.ecommerce.features.auth.repositories.UserRepository;
import com.sba301.ecommerce.features.cart.dto.AddressDto;
import com.sba301.ecommerce.features.entities.Address;
import com.sba301.ecommerce.features.entities.User;
import com.sba301.ecommerce.security.user.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import com.sba301.ecommerce.features.order.repository.OrderRepository;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public AddressServiceImpl(AddressRepository addressRepository, UserRepository userRepository, OrderRepository orderRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails details = (CustomUserDetails) principal;
            return userRepository.findById(details.getUser().getId())
                    .orElseThrow(() -> new com.sba301.ecommerce.exception.ResourceNotFoundException("User not found"));
        }
        throw new com.sba301.ecommerce.exception.InvalidCredentialsException("User not authenticated");
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressDto> getMyAddresses() {
        User currentUser = getCurrentUser();
        List<Address> addresses = addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(currentUser.getId());
        return addresses.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddressDto addAddress(AddressDto dto) {
        User currentUser = getCurrentUser();
        List<Address> existingAddresses = addressRepository.findByUserId(currentUser.getId());

        Address address = new Address();
        address.setUser(currentUser);
        address.setRecipientName(dto.getRecipientName());
        address.setPhone(dto.getPhone());
        address.setProvince(dto.getProvince());
        address.setDistrict(dto.getDistrict());
        address.setWard(dto.getWard());
        address.setStreet(dto.getStreet());

        boolean shouldBeDefault = (dto.getIsDefault() != null && dto.getIsDefault()) || existingAddresses.isEmpty();
        address.setIsDefault(shouldBeDefault);

        if (shouldBeDefault && !existingAddresses.isEmpty()) {
            for (Address existing : existingAddresses) {
                if (existing.getIsDefault()) {
                    existing.setIsDefault(false);
                    addressRepository.save(existing);
                }
            }
        }

        Address saved = addressRepository.save(address);
        return toDto(saved);
    }

    @Override
    @Transactional
    public void deleteAddress(Long id) {
        User currentUser = getCurrentUser();
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new com.sba301.ecommerce.exception.ResourceNotFoundException("Không tìm thấy địa chỉ."));

        if (!address.getUser().getId().equals(currentUser.getId())) {
            throw new com.sba301.ecommerce.exception.BadRequestException("Bạn không có quyền xóa địa chỉ này.");
        }

        // Unlink address from historical orders before deletion to prevent foreign key constraints
        orderRepository.unlinkShippingAddress(id);

        boolean wasDefault = Boolean.TRUE.equals(address.getIsDefault());
        addressRepository.delete(address);

        if (wasDefault) {
            List<Address> remaining = addressRepository.findByUserId(currentUser.getId());
            if (!remaining.isEmpty()) {
                Address newDefault = remaining.get(0);
                newDefault.setIsDefault(true);
                addressRepository.save(newDefault);
            }
        }
    }

    private AddressDto toDto(Address address) {
        AddressDto dto = new AddressDto();
        dto.setId(address.getId());
        dto.setRecipientName(address.getRecipientName());
        dto.setPhone(address.getPhone());
        dto.setProvince(address.getProvince());
        dto.setDistrict(address.getDistrict());
        dto.setWard(address.getWard());
        dto.setStreet(address.getStreet());
        dto.setIsDefault(address.getIsDefault());
        return dto;
    }
}
