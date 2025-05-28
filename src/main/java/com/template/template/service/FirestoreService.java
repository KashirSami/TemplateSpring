package com.template.template.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.template.template.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class FirestoreService {

    public Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    public String addProduct(Product product) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getFirestore().collection("products").document();
        product.setId(docRef.getId()); // Asignar el ID generado automáticamente

        ApiFuture<WriteResult> result = docRef.set(product);
        return result.get().getUpdateTime().toString();
    }

    public List<Product> getAllProducts() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> query = getFirestore().collection("products").get();
        return query.get().getDocuments().stream()
                .map(document -> document.toObject(Product.class))
                .collect(Collectors.toList());
    }
}