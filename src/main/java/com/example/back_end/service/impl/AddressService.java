package com.example.back_end.service.impl;

import com.example.back_end.model.dto.address.AddressDTO;
import com.example.back_end.model.dto.user.UserDTO;
import com.example.back_end.model.entity.Address;
import com.example.back_end.model.entity.Order;
import com.example.back_end.model.entity.User;
import com.example.back_end.model.request.AddressRequest;
import com.example.back_end.model.response.AddressResponse;
import com.example.back_end.model.response.OrderResponse;
import com.example.back_end.repository.AddressRepository;
import com.example.back_end.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@Slf4j
public class AddressService {
    ModelMapper modelMapper;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserRepository userRepository;

    public String addAddress(AddressRequest addressRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();

            Address address = new Address();

            address.setFullName(addressRequest.getFullName());
            address.setCity(addressRequest.getCity());
            address.setDistrict(addressRequest.getDistrict());
            address.setWard(addressRequest.getWard());
            address.setPhoneNumber(addressRequest.getPhoneNumber());

            User user = userRepository.findById(currentUser.getId()).orElseThrow(() -> new RuntimeException("User not found"));
            address.setAddressUser(user);

            addressRepository.save(address);
            return "Create Address Successfully...";
        }catch (Exception e){
            e.printStackTrace();
            return "Error while creating address!!!";
        }
    }
    public List<AddressDTO> findListAddress(){
        try {
            List<Address> addresses = addressRepository.findAll();
            return addresses.stream()
                    .map(address -> modelMapper.map(address, AddressDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Transactional
    public List<AddressDTO> getAddressByCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();
            Long userId = currentUser.getId();

            if (userId == null) {
                throw new RuntimeException("User ID is null");
            }

            List<Address> addresses = addressRepository.findByAddressUser_Id(userId);

            return addresses.stream()
                    .map(address -> modelMapper.map(address, AddressDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while fetching addresses for the current user: " + e.getMessage());
        }
    }

    public String updateAddress(Long id, AddressRequest addressRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();

            Address address = addressRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Address not found"));

            // Ensure the address belongs to the current user
            if (!address.getAddressUser().getId().equals(currentUser.getId())) {
                return "Unauthorized to update this address";
            }

            address.setFullName(addressRequest.getFullName());
            address.setCity(addressRequest.getCity());
            address.setDistrict(addressRequest.getDistrict());
            address.setWard(addressRequest.getWard());
            address.setPhoneNumber(addressRequest.getPhoneNumber());

            addressRepository.save(address);
            return "Update Address Successfully...";
        } catch (Exception e) {
            e.printStackTrace(); // For debugging purposes, you can replace this with a logger
            return "Error while updating address!!!";
        }
    }

    public String deleteAddress(Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();

            Address address = addressRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Address not found"));

            // Ensure the address belongs to the current user
            if (!address.getAddressUser().getId().equals(currentUser.getId())) {
                return "Unauthorized to delete this address";
            }

            addressRepository.delete(address);
            return "Delete Address Successfully...";
        } catch (Exception e) {
            e.printStackTrace(); // For debugging purposes, you can replace this with a logger
            return "Error while deleting address!!!";
        }
    }
}
