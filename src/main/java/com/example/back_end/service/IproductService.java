package com.example.back_end.service;

import com.example.back_end.exception.ProductException;
import com.example.back_end.model.entity.Product;

public interface IproductService {
    public Product getSelectedProduct(int id) throws ProductException;
}
