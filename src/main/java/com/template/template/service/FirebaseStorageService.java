package com.template.template.service;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class FirebaseStorageService {

    public String saveProduct(String name, double price)
            throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        WriteResult result = db.collection("products")
                .document()
                .set(Map.of("name", name, "price", price))
                .get();

        return result.getUpdateTime().toString();
    }
}
