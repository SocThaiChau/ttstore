package com.example.back_end.model.request;

import com.example.back_end.model.entity.Product;
import com.example.back_end.model.response.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.PrintStream;
import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ImageRequest implements Serializable {
    private Long id;
    private String url;
    private Product product;
}
