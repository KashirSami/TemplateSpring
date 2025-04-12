package com.template.template.controller;

import com.google.firebase.auth.FirebaseAuthException;
import com.template.template.service.FirebaseAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FirebaseAuthController {
    @Autowired
    private FirebaseAuthService authService;

    @GetMapping("/register")
    public String showRegisterForm() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String email,
            @RequestParam String password,
            RedirectAttributes redirectAttrs) {

        try {
            authService.createUser(email, password);
            redirectAttrs.addAttribute("success", true);
            return "redirect:/auth/login";
        } catch (FirebaseAuthException e) {
            redirectAttrs.addAttribute("error", true);
            redirectAttrs.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
            return "redirect:/auth/register";
        }
    }
}
