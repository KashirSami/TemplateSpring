package com.template.template.service;
import com.template.template.model.Product;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface FirebaseStorageService {
        void saveProduct(Product product) throws ExecutionException, InterruptedException;
        List<Product> getAllProducts() throws ExecutionException, InterruptedException;
        void updateProduct(Product product) throws ExecutionException, InterruptedException;
        List<Product> searchProducts(String query) throws ExecutionException, InterruptedException;
        Product getProductById(String id) throws Exception;
        List<Product> filterByCategory(String category) throws ExecutionException, InterruptedException, Exception;
        List<Product> searchByNameAndCategory(String name, String catageory) throws Exception;
}
