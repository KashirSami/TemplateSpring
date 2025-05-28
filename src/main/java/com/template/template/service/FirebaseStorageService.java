package com.template.template.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.template.template.model.Product;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class FirebaseStorageService {

    public String saveProduct(Product product) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        if (product.getId() == null) {
            product.setId(UUID.randomUUID().toString());
        }

        product.setFechaCreacion(new Date());

        ApiFuture<WriteResult> result = db.collection("products")
                .document(product.getId())
                .set(product);

        return result.get().getUpdateTime().toString();
    }

    public List<Product> searchProducts(String query) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        ApiFuture<QuerySnapshot> future = db.collection("products")
                .whereGreaterThanOrEqualTo("nombre", query)
                .whereLessThanOrEqualTo("nombre", query + "\uf8ff")
                .get();

        return future.get().getDocuments().stream()
                .map(document -> document.toObject(Product.class))
                .collect(Collectors.toList());
    }

    public void updateProduct(String id, Product product) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        Map<String, Object> updates = new HashMap<>();
        updates.put("nombre", product.getNombre());
        updates.put("descripcion", product.getDescripcion());
        updates.put("valor", product.getValor());
        updates.put("categoria", product.getCategoria());
        updates.put("stock", product.getStock());

        db.collection("products")
                .document(id)
                .update(updates);
    }
}