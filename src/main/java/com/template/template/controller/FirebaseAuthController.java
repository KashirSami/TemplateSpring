package com.template.template.controller;

import com.template.template.handler.AdminValidator;
import com.template.template.model.RegisterRequest;
import com.template.template.service.FirebaseAuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class FirebaseAuthController {

    @Autowired
    private FirebaseAuthService authService;

    @Autowired
    private AdminValidator adminValidator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Registro de usuarios
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute RegisterRequest request,
                                      Model model) {
        String result = authService.registerUser(request);
        if ("success".equals(result)) {
            return "redirect:/register?success";
        } else {
            model.addAttribute("errorMessage", result);
            return "register";
        }
    }

    // Login para usuarios y admin
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/debug-auth")
    public String debugAuth(Authentication authentication) {
        return "Roles actuales: " + authentication.getAuthorities();
    }

    @PostMapping("/login")
    public String processLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            if (adminValidator.isAdmin(username, password)) {
                Authentication auth = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        List.of(new SimpleGrantedAuthority("ADMIN"))
                );
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(auth);
                SecurityContextHolder.setContext(context);

                session.setAttribute("SPRING_SECURITY_CONTEXT", context);
                System.out.println("Roles asignados: " + context.getAuthentication().getAuthorities());

                return "redirect:admin/dashboard";
            }

            // 2. Verificar usuario normal con Firebase
            if (authService.loginUser(username, password)) {
                Authentication auth = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        List.of(new SimpleGrantedAuthority("USER"))
                );
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(auth);
                SecurityContextHolder.setContext(context);

                session.setAttribute("SPRING_SECURITY_CONTEXT", context);
                session.setAttribute("userRole", "USER");
                session.setAttribute("userEmail", username);

                return "redirect:/";
            }

            // 3. Si no se logró autenticar
            redirectAttributes.addAttribute("error", true);
            return "redirect:/login";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addAttribute("error", true);
            return "redirect:/login";
        }
    }
}