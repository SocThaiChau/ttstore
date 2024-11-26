package com.example.back_end.controller;

import com.example.back_end.model.entity.Address;
import com.example.back_end.model.request.AddressRequest;
import com.example.back_end.model.response.AddressResponse;
import com.example.back_end.service.impl.AddressService;
import com.example.back_end.service.impl.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/address")
public class AddressController {
    @Autowired
    private AddressService addressService;

    @GetMapping()
    public ResponseEntity<List<Address>> getAllAddresses() {
        List<Address> addresses = addressService.findAllAddress();
        return ResponseEntity.ok(addresses);
    }
    @GetMapping("/my-addresses")
    public ResponseEntity<List<AddressResponse>> getAddressesByCurrentUser() {
        List<AddressResponse> addresses = addressService.getAddressByCurrentUser();
        return ResponseEntity.ok(addresses);
    }
    @PostMapping("/add")
    public ResponseEntity<String> addAddress(@RequestBody AddressRequest addressRequest) {
        String result = addressService.addAddress(addressRequest);
        if (result.equals("Create Address Successfully...")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(500).body(result);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateAddress(@PathVariable Long id, @RequestBody AddressRequest addressRequest) {
        String result = addressService.updateAddress(id, addressRequest);
        if (result.equals("Update Address Successfully...")) {
            return ResponseEntity.ok(result);
        } else if (result.equals("Unauthorized to update this address")) {
            return ResponseEntity.status(403).body(result);
        } else {
            return ResponseEntity.status(500).body(result);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long id) {
        String result = addressService.deleteAddress(id);
        if (result.equals("Delete Address Successfully...")) {
            return ResponseEntity.ok(result);
        } else if (result.equals("Unauthorized to delete this address")) {
            return ResponseEntity.status(403).body(result);
        } else {
            return ResponseEntity.status(500).body(result);
        }
    }

}
