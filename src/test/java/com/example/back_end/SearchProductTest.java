package com.example.back_end;

import com.example.back_end.controller.ProductController;
import com.example.back_end.model.response.ProducListResponse;
import com.example.back_end.model.response.ProductResponse;
import com.example.back_end.service.impl.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SearchProductTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private List<ProductResponse> mockProductList;

    @BeforeEach
    void setUp() {
        // Tạo một danh sách các sản phẩm mẫu
        ProductResponse product1 = new ProductResponse();
        product1.setName("áo mới");

        ProductResponse product2 = new ProductResponse();
        product2.setName("áo 1");

        mockProductList = Arrays.asList(product1, product2);
    }

    @Test
    void testSearchProducts_ShouldReturnListOfProducts() {
        // Thiết lập mock: Khi gọi service tìm kiếm với từ khóa "product", trả về danh sách sản phẩm mẫu
        String keyword = "áo";
        when(productService.search(keyword)).thenReturn(mockProductList);

        // Gọi phương thức controller
        ProducListResponse response = productController.searchProducts(keyword);

        // Kiểm tra kết quả
        assertNotNull(response);
        assertEquals(mockProductList.size(), response.getData().size());
        assertEquals("áo mới", response.getData().get(0).getName());
        assertEquals("áo 1", response.getData().get(1).getName());

        // Kiểm tra xem phương thức search của productService có được gọi một lần
        verify(productService, times(1)).search(keyword);
    }

    @Test
    void testSearchProducts_EmptyKeyword_ShouldThrowException() {
        // Kiểm tra với từ khóa trống
        String keyword = "";

        // Gọi phương thức và kiểm tra ngoại lệ
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productController.searchProducts(keyword);
        });

        // Kiểm tra thông báo ngoại lệ
        assertEquals("Vui lòng nhập từ khóa tìm kiếm.", exception.getMessage());
    }

    @Test
    void testSearchProducts_ShouldReturnNoResultsMessage_WhenNoProductsFound() {
        // Kiểm tra với từ khóa không tìm thấy sản phẩm
        String keyword = "nonexistent";
        when(productService.search(keyword)).thenReturn(Arrays.asList());

        ProducListResponse response = productController.searchProducts(keyword);

        assertNotNull(response);
        assertEquals("Không có sản phẩm nào khớp với từ khóa: nonexistent", response.getMessage());
        assertTrue(response.getData().isEmpty());

        // Kiểm tra phương thức search của productService có được gọi 1 lần
        verify(productService, times(1)).search(keyword);
    }

}
