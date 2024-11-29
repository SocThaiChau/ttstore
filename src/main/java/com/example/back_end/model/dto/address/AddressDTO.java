package com.example.back_end.model.dto.address;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Hidden
public class AddressDTO {
    private Long id;

    private String fullName;

    private String phoneNumber;

    private String city;

    private String district;

    private String ward;

    private Boolean isDefault;
}
