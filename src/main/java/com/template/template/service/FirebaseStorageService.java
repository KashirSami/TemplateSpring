package com.template.template.service;

import com.template.template.model.Product;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public interface FirebaseStorageService {

    void uploadProduct(Product product);
    List<Product> searchProducts(String query) throws ExecutionException, InterruptedException;
    void updateProduct(String id, Product product) throws ExecutionException, InterruptedException;
}
