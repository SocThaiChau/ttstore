package com.example.back_end;

import com.example.back_end.controller.ProductController;
import com.example.back_end.exception.UserException;
import com.example.back_end.model.entity.Category;
import com.example.back_end.model.entity.Product;
import com.example.back_end.model.request.ProductRequest;
import com.example.back_end.model.response.AuthenticationResponse;
import com.example.back_end.response.ResponseObject;
import com.example.back_end.service.impl.AuthenticationService;
import com.example.back_end.service.impl.CategoryService;
import com.example.back_end.service.impl.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AddProductTest {

    @InjectMocks
    private ProductController productController; // Controller cần kiểm tra

    @Mock
    private ProductService productService; // Mô phỏng ProductService

    @Mock
    private CategoryService categoryService; // Mô phỏng CategoryService

    @Mock
    private AuthenticationService authenticationService; // Mô phỏng AuthenticationService

    @Mock
    private HttpServletRequest request; // Mô phỏng HttpServletRequest

    private ProductRequest productRequest;


    @BeforeEach
    void setUp() {
        // Khởi tạo các mock trước khi mỗi test chạy
        MockitoAnnotations.openMocks(this);

        // Khởi tạo đối tượng ProductRequest dùng chung cho các test
        productRequest = new ProductRequest();
        productRequest.setName("Sản phẩm thử");
        productRequest.setDescription("Mô tả sản phẩm thử");
        productRequest.setPrice(100.0);
        productRequest.setQuantity(10);
        productRequest.setCategoryId(1L);
    }

    @Test
    void testCreateProduct_Success() throws UserException {
        // Mock dữ liệu trả về từ AuthenticationService
        when(authenticationService.authenticate(any(), eq(request))).thenReturn(new AuthenticationResponse());

        // Mock categoryService trả về category hợp lệ
        Category mockCategory = new Category();
        mockCategory.setId(1L);
        mockCategory.setName("Thực phẩm");
        when(categoryService.getCategoryById(anyLong())).thenReturn(mockCategory);

        // Mock productService trả về sản phẩm vừa tạo
        Product mockProduct = new Product();
        mockProduct.setName("Sản phẩm thử");
        mockProduct.setDescription("Mô tả sản phẩm thử");
        mockProduct.setPrice(100.0);
        mockProduct.setQuantity(10);
        mockProduct.setCategory(mockCategory);

        when(productService.createProduct(any(Product.class))).thenReturn(mockProduct);

        // Gọi phương thức createProduct và kiểm tra phản hồi
        ResponseEntity<ResponseObject> response = productController.createProduct(productRequest, request);

        // Kiểm tra mã trạng thái và phản hồi
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Success", response.getBody().getStatus());

        // Kiểm tra nếu data là Map và có chứa key "name"
        Object data = response.getBody().getData();

        // Kiểm tra data có phải là Map không
        assertTrue(data instanceof Map, "Data should be a Map");

        // Ép kiểu về Map và kiểm tra key
        Map<String, Object> dataMap = (Map<String, Object>) data;
        assertTrue(dataMap.containsKey("name"), "Data should contain 'name' key");
        assertEquals("Sản phẩm thử", dataMap.get("name"));
        assertTrue(dataMap.containsKey("Description"), "Data should contain 'Description' key");
        assertEquals("Mô tả sản phẩm thử", dataMap.get("Description"));
        assertTrue(dataMap.containsKey("price"), "Data should contain 'price' key");
        assertEquals(100.0, dataMap.get("price"));
        assertTrue(dataMap.containsKey("quantity"), "Data should contain 'quantity' key");
        assertEquals(10, dataMap.get("quantity"));
    }
    @Test
    void testCreateProduct_Failure_InvalidCategory() throws UserException {
        // Mock dữ liệu trả về từ AuthenticationService
        when(authenticationService.authenticate(any(), eq(request))).thenReturn(new AuthenticationResponse());

        // Giả sử category không tồn tại hoặc trả về null
        when(categoryService.getCategoryById(anyLong())).thenReturn(null);

        // Gọi phương thức createProduct và kiểm tra lỗi
        ResponseEntity<ResponseObject> response = productController.createProduct(productRequest, request);

        // Kiểm tra mã trạng thái và phản hồi lỗi
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ERROR", response.getBody().getStatus());
    }
    @Test
    void testCreateProduct_Failure() throws UserException {
        // Mô phỏng các dịch vụ để giả lập trường hợp thất bại
        when(authenticationService.authenticate(any(), eq(request)))
                .thenThrow(new RuntimeException("Invalid authorization header."));

        // Gọi phương thức createProduct và kiểm tra phản hồi lỗi
        ResponseEntity<ResponseObject> response = productController.createProduct(productRequest, request);

        // Kiểm tra phản hồi lỗi
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ERROR", response.getBody().getStatus());
        assertEquals("Invalid authorization header.", response.getBody().getMessage());
    }
}
