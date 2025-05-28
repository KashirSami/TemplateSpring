package com.template.template.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() throws IOException {
        // Ruta al archivo JSON descargado de Firebase (ajústala)
        FileInputStream serviceAccount =
                new FileInputStream("src/main/resources/proyectofinal.json");

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://TU-PROYECTO.firebaseio.com") // Reemplaza con tu URL
                .build();

        FirebaseApp.initializeApp(options);
    }

    @Bean
    public Firestore firestore() throws IOException {
        // Apunta a tu archivo serviceAccountKey.json
        FileInputStream serviceAccount = new FileInputStream("src/main/resources/firebase-service-account.json");
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
        FirebaseApp.initializeApp(options);
        return FirestoreClient.getFirestore();
    }
}

