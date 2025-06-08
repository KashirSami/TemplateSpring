package com.template.template.service;

import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.template.template.database.FirebaseRestLogin;
import com.template.template.model.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FirebaseAuthService {

    @Autowired
    private FirebaseRestLogin firebaseRestLogin;

    public String registerUser(RegisterRequest request) {
        // 1) Validar confirmaciones antes de enviar a Firebase
        if (!request.getEmail().equals(request.getConfirmEmail())) {
            return "Los correos no coinciden.";
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return "Las contraseñas no coinciden.";
        }
        if (request.getPassword().length() < 6) {
            return "La contraseña debe tener al menos 6 caracteres.";
        }

        try {
            UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest()
                    .setEmail(request.getEmail())
                    .setEmailVerified(false)
                    .setPassword(request.getPassword())
                    .setDisplayName(request.getNombre())
                    .setPhoneNumber(request.getTelefono());

            FirebaseAuth.getInstance().createUser(createRequest);
            return "success";

        } catch (FirebaseAuthException e) {

            // Mapea los posibles códigos de AuthErrorCode a tu mensaje de usuario
            switch (e.getAuthErrorCode()) {
                case EMAIL_ALREADY_EXISTS:
                    return "Ya existe una cuenta con ese correo electrónico.";
                case PHONE_NUMBER_ALREADY_EXISTS:
                    return "Ese número de teléfono ya está registrado.";
                case UID_ALREADY_EXISTS:
                    return "El identificador de usuario ya existe.";
                default:
                    // Para otros códigos (por ejemplo, QUOTA_EXCEEDED, etc.), devolvemos el mensaje genérico
                    return "Error registrando usuario: " + e.getAuthErrorCode().name();
            }
        } catch (Exception e) {
            // Cualquier otra excepción inesperada
            return "Error inesperado al registrar: " + e.getMessage();
        }

    }
    public boolean loginUser(String email, String password) throws FirebaseRestLogin.FirebaseRestLoginException {
        return firebaseRestLogin.loginWithEmailPassword(email, password);
    }

}
