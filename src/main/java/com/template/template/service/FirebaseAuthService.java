package com.template.template.service;

import com.google.api.client.util.Value;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.template.template.database.FirebaseRestLogin;
import com.template.template.model.RegisterRequest;
import org.springframework.stereotype.Service;

@Service
public class FirebaseAuthService {

        public String registerUser(RegisterRequest request) {
        try {
            UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest()
                    .setEmail(request.getEmail())
                    .setPassword(request.getPassword())
                    .setDisplayName(request.getNombre())
                    .setPhoneNumber(request.getTelefono());

            FirebaseAuth.getInstance().createUser(createRequest);
            return "success";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public void deleteUser(String uid) throws FirebaseAuthException {
        FirebaseAuth.getInstance().deleteUser(uid);
    }

    public boolean loginUser(String email, String password) throws FirebaseAuthException {
        return FirebaseRestLogin.loginWithEmailPassword(email, password);
    }


}
