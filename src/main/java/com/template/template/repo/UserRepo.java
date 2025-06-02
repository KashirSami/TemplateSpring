package com.template.template.repo;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.template.template.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Repository
public class UserRepo {

    private static final String COLLECTION = "users";

    @Autowired
    private Firestore firestore;

    public Optional<User> findByEmail(String email) throws ExecutionException {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION).document(email);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot snapshot = future.get();
            if (snapshot.exists()) {
                User u = snapshot.toObject(User.class);
                return Optional.ofNullable(u);
            } else {
                return Optional.empty();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error leyendo usuario de Firestore", e);
        }
    }

    public void save(User user) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION).document(user.getEmail());
            ApiFuture<WriteResult> writeResult = docRef.set(user);
            writeResult.get(); // Espera la confirmación
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error guardando usuario en Firestore", e);
        }
    }
}
