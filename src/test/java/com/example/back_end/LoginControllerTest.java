package com.example.back_end;

import com.example.back_end.controller.LoginController;
import com.example.back_end.model.dto.user.UserDTO;
import com.example.back_end.model.entity.Role;
import com.example.back_end.model.request.AuthenticationRequest;
import com.example.back_end.model.response.AuthenticationResponse;
import com.example.back_end.service.impl.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Date;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class LoginControllerTest {

    @InjectMocks
    private LoginController loginController; // Controller sẽ nhận mock từ AuthenticationService

    @Mock
    private AuthenticationService authenticationService; // Mock AuthenticationService

    @Mock
    private HttpServletRequest httpServletRequest; // Mock HttpServletRequest

    private AuthenticationRequest authenticationRequest;

    @BeforeEach
    void setUp() {
        // Khởi tạo các mock trước khi mỗi test chạy
        MockitoAnnotations.openMocks(this);

        // Khởi tạo đối tượng AuthenticationRequest dùng chung trong các test
        authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("test@example.com");
        authenticationRequest.setPassword("password");
    }

    @Test
    void testLogin_Success() {
        // Mock dữ liệu trả về từ AuthenticationService
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setToken("sampleToken");
        authenticationResponse.setEmail("test@example.com");
        authenticationResponse.setId(1L);
        authenticationResponse.setName("Test User");
        authenticationResponse.setPhoneNumber("123456789");
        authenticationResponse.setDob(new Date());
        authenticationResponse.setRole(new Role());
        authenticationResponse.setUserDTO(new UserDTO());

        // Thiết lập hành vi mock cho phương thức authenticate của AuthenticationService
        when(authenticationService.authenticate(authenticationRequest, httpServletRequest))
                .thenReturn(authenticationResponse);

        // Gọi phương thức login của controller
        ResponseEntity<AuthenticationResponse> response = loginController.login(authenticationRequest, httpServletRequest);

        // Kiểm tra mã trạng thái và phản hồi
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("sampleToken", response.getBody().getToken());
        assertEquals("test@example.com", response.getBody().getEmail());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Test User", response.getBody().getName());
        assertEquals("123456789", response.getBody().getPhoneNumber());
        assertNotNull(response.getBody().getDob());
        assertNotNull(response.getBody().getRole());
    }

    @Test
    void testLogin_Failure_InvalidCredentials() {
        // Khi thông tin đăng nhập không hợp lệ, giả sử service trả về null hoặc throw exception
        when(authenticationService.authenticate(authenticationRequest, httpServletRequest))
                .thenThrow(new RuntimeException("Invalid username/password supplied"));

        // Gọi phương thức login của controller và kiểm tra lỗi
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            loginController.login(authenticationRequest, httpServletRequest);
        });

        assertEquals("Invalid username/password supplied", exception.getMessage());
    }
}
