package com.example.back_end.config;

import com.example.back_end.auth.JwtService;
import com.example.back_end.exception.UserException;
import com.example.back_end.model.entity.User;
import com.example.back_end.service.impl.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

@AllArgsConstructor
@Data
public class ExtractUser {
    private Long userId;
    private boolean isEnabled;
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    public ExtractUser(String token, UserService userService, JwtService jwtService) throws UserException {
        this.userService = userService;
        this.jwtService = jwtService;
        this.isEnabled = false;

        try {
            String email = jwtService.extractEmail(token);
            User user = userService.getUserByEmail(email);
            if (user != null) {
                this.userId = user.getId();
                this.isEnabled = true;
            }
        } catch (Exception e) {
            throw new UserException("Failed to extract user information from token.", e);
        }
    }
}