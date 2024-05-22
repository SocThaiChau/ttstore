package com.example.back_end.service.impl;

import com.example.back_end.auth.JwtService;
import com.example.back_end.model.entity.User;
import com.example.back_end.model.request.AuthenticationRequest;
import com.example.back_end.model.response.AuthenticationResponse;
import com.example.back_end.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletRequest req){
        HttpSession session = req.getSession(true);
        try{
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
            );
            authenticationManager.authenticate(token);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        }catch (AuthenticationException e){
            throw new RuntimeException("Invalid username/password supplied");
        }
        User user = userRepository.getUserByEmail(request.getEmail());
        String token = jwtService.generateToken(user);
        AuthenticationResponse authenticateResponse = new AuthenticationResponse();
        authenticateResponse.setToken(token);
        authenticateResponse.setEmail(user.getEmail());
//        authenticateResponse.setDob(user.getDob());
//        authenticateResponse.setGender(user.getGender());
        return authenticateResponse;
    }

}
