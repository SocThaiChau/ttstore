package com.example.back_end.service.impl;

import com.example.back_end.exception.ProductException;
import com.example.back_end.model.dto.SalesDTO;
import com.example.back_end.model.entity.Image;
import com.example.back_end.model.entity.Product;
import com.example.back_end.repository.OrderItemRepository;

import com.example.back_end.model.entity.User;
import com.example.back_end.model.request.ProductRequest;
import com.example.back_end.model.response.ImageResponse;
import com.example.back_end.model.response.ProductResponse;
import com.example.back_end.model.response.ReviewResponse;
import com.example.back_end.repository.ImageRepository;
import com.example.back_end.repository.ProductRepository;
import com.example.back_end.service.IproductService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductService implements IproductService {
    @Autowired
    private final ProductRepository productRepository ;

    @Autowired
    private ImageRepository imageRepository;

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
    // In ProductService

    public int getTotalSoldProductsByUser(Long userId) {
        return productRepository.findByUser_IdAndSoldGreaterThan(userId, 0).stream()
                .mapToInt(Product::getSold)
                .sum();

    }

    public double getTotalRevenueByUser(Long userId) {
        List<Product> products = productRepository.findByUser_IdAndSoldGreaterThan(userId, 0);
        return products.stream()
                .mapToDouble(p -> p.getSold() * p.getPrice())
                .sum();

    }
    public SalesDTO getSalesByUserAndProduct(Long userId, Long productId) throws ProductException {
        List<Object[]> salesData = productRepository.getSalesByUserAndProduct(userId, productId);
        if (salesData.isEmpty()) {
            throw new ProductException("No product found with ID: " + productId + " for user with ID: " + userId);
        }
        Object[] data = salesData.get(0);
        SalesDTO salesDTO = new SalesDTO();
        salesDTO.setProductName((String) data[0]);
        salesDTO.setTotalSales((long) data[1]);
        salesDTO.setTotalRevenue((double) data[2]);

        return salesDTO;
    }
    public List<SalesDTO> getSalesByUser(Long userId) {
        List<Object[]> salesData = productRepository.getSalesByUser(userId);
        List<SalesDTO> salesDTOs = new ArrayList<>();

        for (Object[] data : salesData) {
            SalesDTO salesDTO = new SalesDTO();
            salesDTO.setProductName((String) data[0]);
            salesDTO.setTotalSales((long) data[1]);
            salesDTO.setTotalRevenue((double) data[2]);
            salesDTOs.add(salesDTO);
        }

        return salesDTOs;
    }

    public void addImagesToProduct(Product product, List<Image> images) {
        images.forEach(image -> {
            image.setProduct(product);
            imageRepository.save(image);
        });
    }

    public List<ProductResponse> getAllProductsResponse() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    private ProductResponse mapToProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setPromotionalPrice(product.getPromotionalPrice());
        response.setQuantity(product.getQuantity());
        response.setQuantityAvailable(product.getQuantityAvailable());
        response.setNumberOfRating(product.getNumberOfRating());
        response.setFavoriteCount(product.getFavoriteCount());
        response.setSold(product.getSold());
        response.setIsActive(product.getIsActive());
        response.setIsSelling(product.getIsSelling());
        response.setRating(product.getRating());
        response.setCreatedBy(product.getCreatedBy());
        response.setLastModifiedBy(product.getLastModifiedBy());
        response.setCreatedDate(product.getCreatedDate());
        response.setLastModifiedDate(product.getLastModifiedDate());
        response.setUrl(product.getUrl());
        response.setUserId(product.getUser().getId());

        // Map images
        if (product.getImages() != null) {
            List<ImageResponse> imageResponses = product.getImages().stream()
                    .map(image -> {
                        ImageResponse imageResponse = new ImageResponse();
                        imageResponse.setId(image.getId());
                        imageResponse.setUrl(image.getUrl());
                        return imageResponse;
                    })
                    .collect(Collectors.toList());
            response.setImages(imageResponses);
        }

        // Map reviews
        if (product.getProductReviewList() != null) {
            List<ReviewResponse> reviewResponses = product.getProductReviewList().stream()
                    .map(review -> {
                        ReviewResponse reviewResponse = new ReviewResponse();
                        reviewResponse.setId(review.getId());
                        reviewResponse.setComment(review.getContent());
                        // Map other fields as needed
                        return reviewResponse;
                    })
                    .collect(Collectors.toList());
            response.setProductReviewList(reviewResponses);
        }

        return response;
    }

    public ProductResponse getProductByIdDetail(Long productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            return mapToProductResponses(product);
        }
        return null; // or throw an exception if desired behavior is to throw not found
    }

    private ProductResponse mapToProductResponses(Product product) {
        ProductResponse response = new ProductResponse();
        // Map fields from product to response
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setPromotionalPrice(product.getPromotionalPrice());
        response.setQuantity(product.getQuantity());
        response.setQuantityAvailable(product.getQuantityAvailable());
        response.setNumberOfRating(product.getNumberOfRating());
        response.setFavoriteCount(product.getFavoriteCount());
        response.setSold(product.getSold());
        response.setIsActive(product.getIsActive());
        response.setIsSelling(product.getIsSelling());
        response.setRating(product.getRating());
        response.setCreatedBy(product.getCreatedBy());
        response.setLastModifiedBy(product.getLastModifiedBy());
        response.setCreatedDate(product.getCreatedDate());
        response.setLastModifiedDate(product.getLastModifiedDate());
        response.setUrl(product.getUrl());
        response.setUserId(product.getUser().getId());
        // Set images and other related fields if needed
        return response;
    }
}
