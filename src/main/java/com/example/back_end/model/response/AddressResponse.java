package com.example.back_end.model.response;

import com.example.back_end.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponse implements Serializable {

    private Long id;

    private String fullName;

    private String phoneNumber;

    private String city;

    private String district;

    private String ward;

    private String orderDetail;

    private Boolean isDefault;

    private String userId;

}
