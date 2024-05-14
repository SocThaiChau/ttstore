package com.example.back_end.model.response;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse implements Serializable {
    private String token;
    private String email;

}
