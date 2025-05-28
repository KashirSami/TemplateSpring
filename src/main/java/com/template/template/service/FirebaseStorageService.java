package com.template.template.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.template.template.model.Product;
import com.template.template.model.Product;
import org.springframework.stereotype.Service;

import java.util.*;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public interface FirebaseStorageService {

    void uploadProduct(Product product);
    List<Product> searchProducts(String query) throws ExecutionException, InterruptedException;
    void updateProduct(Product product) throws ExecutionException, InterruptedException;
    void saveProduct(Product product) throws ExecutionException, InterruptedException;
    void deleteProduct(String id) throws ExecutionException, InterruptedException;
}
