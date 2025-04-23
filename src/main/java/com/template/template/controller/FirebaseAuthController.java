package com.template.template.controller;

import com.google.firebase.auth.FirebaseAuthException;
import com.template.template.model.RegisterRequest;
import com.template.template.service.FirebaseAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FirebaseAuthController {
    @Autowired
    private FirebaseAuthService authService;

    //Registro de usuarios
    @GetMapping("/auth/register")
    public String showRegisterForm() {
        return "auth/register";
    }

    @PostMapping("/auth/register")
    public String procesarRegistro(@ModelAttribute RegisterRequest request, Model model) {
        String resultado = authService.registrarUsuario(request);

        if (resultado.equals("success")) {
            return "redirect:/register?success";
        } else {
            model.addAttribute("errorMessage", resultado);
            return "auth/register";
        }
    }


    //Login de usuarios
    @GetMapping("/auth/login")
    public String showLoginForm() {
        return "login";
    }
    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        Model model) {
        try {
            boolean loginExitoso = authService.loginUser(email, password);
            if (loginExitoso) {
                return "redirect:/index";
            } else {
                model.addAttribute("error", "Correo o contraseña incorrectos.");
                return "login";
            }
        } catch (FirebaseAuthException e) {
            model.addAttribute("error", "Error de autenticación: " + e.getMessage());
            return "login";
        }
    }

}
