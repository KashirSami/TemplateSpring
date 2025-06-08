package com.template.template.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions.Builder builder = FirebaseOptions.builder();

                String jsonContent = System.getenv("FIREBASE_CONFIG");
                String path = System.getenv("FIREBASE_CONFIG_PATH");
                InputStream credentialsStream;

                if (jsonContent != null && !jsonContent.isBlank()) {
                    credentialsStream = new ByteArrayInputStream(jsonContent.getBytes(StandardCharsets.UTF_8));
                } else if (path != null && !path.isBlank()) {
                    credentialsStream = Files.newInputStream(Path.of(path));
                } else {
                    credentialsStream = new ClassPathResource("proyectofinal-ca5b2.json").getInputStream();
                }

                String projectId = System.getenv().getOrDefault("FIREBASE_PROJECT_ID", "proyectofinal-ca5b2");

                FirebaseOptions options = builder
                        .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                        .setProjectId(projectId)
                        .build();
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error inicializando Firebase", e);
        }
    }

    @Bean
    @Primary
    public Firestore firestore(){
        return FirestoreClient.getFirestore();
    }
}

