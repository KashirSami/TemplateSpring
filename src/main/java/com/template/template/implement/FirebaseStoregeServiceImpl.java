package com.template.template.implement;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.template.template.model.Product;
import com.template.template.service.FirebaseStorageService;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class FirebaseStoregeServiceImpl implements FirebaseStorageService {

    private final Firestore firestore;

    public FirebaseStoregeServiceImpl(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public void uploadProduct(Product product) {
        ApiFuture<WriteResult> future = firestore
                .collection("products")
                .document(product.getId())
                .set(product);
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Product> searchProducts(String query) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore
                .collection("products")
                .whereGreaterThanOrEqualTo("nombre", query)
                .whereLessThanOrEqualTo("nombre", query + "\uf8ff")
                .get();
        return future.get()
                .getDocuments()
                .stream()
                .map(doc -> doc.toObject(Product.class))
                .collect(Collectors.toList());
    }

    @Override
    public void updateProduct(Product product) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore
                .collection("products")
                .document(product.getId())
                .set(product);
    }

    @Override
    public void saveProduct(Product product) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore
                .collection("products")
                .document(product.getId());
        ApiFuture<WriteResult> writeResult = docRef.set(product);
        System.out.println("Product saved with id: " + writeResult.get().getUpdateTime());
    }

    @Override
    public void deleteProduct(String id) throws ExecutionException, InterruptedException {

    }
}
