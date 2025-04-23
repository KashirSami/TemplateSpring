package com.template.template.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.template.template.database.FirebaseRestLogin;
import com.template.template.model.RegisterRequest;
import org.springframework.stereotype.Service;

@Service
public class FirebaseAuthService {

    public String registrarUsuario(RegisterRequest request) {
        try {
            UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest()
                    .setEmail(request.getEmail())
                    .setPassword(request.getPassword())
                    .setDisplayName(request.getNombre())
                    .setPhoneNumber(request.getTelefono());

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(createRequest);
            FirebaseAuth.getInstance().generateEmailVerificationLink(request.getEmail());

            return "success";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String createUser(String email, String password) throws FirebaseAuthException {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password)
                .setEmailVerified(false);

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
        return userRecord.getUid();
    }

    public void deleteUser(String uid) throws FirebaseAuthException {
        FirebaseAuth.getInstance().deleteUser(uid);
    }

    public boolean loginUser(String email, String password) throws FirebaseAuthException {
        // NOTA: Firebase Admin SDK NO permite verificar contraseñas directamente por seguridad
        // Alternativas:
        // 1. Autenticación se hace en frontend con Firebase JS SDK (como hiciste antes)
        // 2. Backend verifica con Firebase REST API (aquí te muestro cómo)

        // Esta opción usa Firebase REST API para verificar las credenciales:
        return FirebaseRestLogin.loginWithEmailPassword(email, password);
    }
}
