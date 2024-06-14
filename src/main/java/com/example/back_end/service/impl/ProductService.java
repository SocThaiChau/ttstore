package com.example.back_end.service.impl;

import com.example.back_end.exception.ProductException;
import com.example.back_end.model.entity.Product;
import com.example.back_end.model.entity.User;
import com.example.back_end.model.request.ProductRequest;
import com.example.back_end.repository.ProductRepository;
import com.example.back_end.service.IproductService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductService implements IproductService {
    @Autowired
    private final ProductRepository productRepository ;

    @Override
    public Product getSelectedProduct(int id) throws ProductException {
        return productRepository.findById(id).orElseThrow(()-> new ProductException("The required product not found"));
    }
    public Product getProductById(Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(Math.toIntExact(productId));
        return optionalProduct.orElse(null);
    }
    public List<Product> getAllProducts (){return productRepository.findAll();}
    public List<Product> getUserProducts(Long userId) {
        // Triển khai logic để truy vấn danh sách sản phẩm của người dùng dựa trên userId
        // Ví dụ:
        return productRepository.findByUserId(userId);
    }
    public Product updateProduct(Product updatedProduct) {
        // Lấy sản phẩm hiện tại từ cơ sở dữ liệu
        Product existingProduct = getProductById(updatedProduct.getId());

        if (existingProduct != null) {
            // Cập nhật thông tin sản phẩm
            existingProduct.setName(updatedProduct.getName());
            existingProduct.setDescription(updatedProduct.getDescription());
            existingProduct.setPrice(updatedProduct.getPrice());
            existingProduct.setPromotionalPrice(updatedProduct.getPromotionalPrice());
            existingProduct.setQuantity(updatedProduct.getQuantity());
            existingProduct.setQuantityAvailable(updatedProduct.getQuantityAvailable());
            existingProduct.setCategory(updatedProduct.getCategory());

            // Thực hiện cập nhật sản phẩm trong cơ sở dữ liệu
            existingProduct = productRepository.save(existingProduct);
        }

        return existingProduct;
    }
    public List<Product> searchProducts(String keyword) {
        List<Product> products = productRepository.findByKeyword(keyword);
        return products;
    }
    public Product createProduct(Product product){return productRepository.save(product);}

}
