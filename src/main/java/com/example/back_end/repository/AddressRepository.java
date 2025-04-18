package com.example.back_end.repository;

import com.example.back_end.model.entity.Address;
import com.example.back_end.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByAddressUser_Id(Long userId);
}
