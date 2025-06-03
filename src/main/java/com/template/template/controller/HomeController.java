package com.template.template.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.template.template.service.FirebaseAuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class HomeController {

    @Autowired
    private FirebaseAuthService authService;

    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        // Obtiene el email del usuario de la sesión
        String userEmail = (String) session.getAttribute("userEmail");

        if (userEmail != null) {
            try {
                // Obtiene los datos del usuario de Firebase
                UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(userEmail);
                
            } catch (FirebaseAuthException e) {
                model.addAttribute("error", "Error al cargar datos del usuario");
            }
        }

        return "index";
    }
}