package com.example.back_end.controller;

import com.example.back_end.model.request.AuthenticationRequest;
import com.example.back_end.model.response.AuthenticationResponse;
import com.example.back_end.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class LoginController {
    private final AuthenticationService authenticationService;


    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request, HttpServletRequest req){
        return ResponseEntity.ok(authenticationService.authenticate(request, req));
    }

}
