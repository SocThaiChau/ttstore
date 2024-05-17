package com.example.back_end.service.impl;

import com.example.back_end.exception.ProductException;
import com.example.back_end.model.entity.Product;
import com.example.back_end.repository.ProductRepository;
import com.example.back_end.service.IproductService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@AllArgsConstructor
public class ProductService implements IproductService {
    @Autowired
    private final ProductRepository productRepository ;

    @Override
    public Product getSelectedProduct(int id) throws ProductException {
        return productRepository.findById(id).orElseThrow(()-> new ProductException("The required product not found"));
    }

    public List<Product> getAllProducts (){return productRepository.findAll();}
    public Product createProduct(Product product){return productRepository.save(product);}
    public List<Product>searchProducts(String keyword){return productRepository.findBydescriptionContaining(keyword);}
}
