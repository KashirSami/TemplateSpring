package com.template.template.controller;

import com.template.template.database.FirebaseRestLogin;
import com.template.template.handler.AdminValidator;
import com.template.template.model.RegisterRequest;
import com.template.template.service.FirebaseAuthService;
import com.template.template.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
public class FirebaseAuthController {

    @Autowired
    private FirebaseAuthService authService;

    @Autowired
    private AdminValidator adminValidator;

    @Autowired
    private UserService userService;

    // Registro de usuarios
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute RegisterRequest request,
                                      Model model) throws ExecutionException {
        if (!request.getEmail().equals(request.getConfirmEmail())) {
            model.addAttribute("errorMessage", "Los correos no coinciden.");
            return "register";
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            model.addAttribute("errorMessage", "Las contraseñas no coinciden.");
            return "register";
        }
        String authResult = authService.registerUser(request);
        if (!"success".equals(authResult)) {
            model.addAttribute("errorMessage", authResult);
            return "register";
        }

        String result = userService.registerUser(
                request.getNombre(),
                request.getEmail(),
                request.getPassword(),
                request.getTelefono()
        );
        if (!"success".equals(result)) {
            model.addAttribute("errorMessage", result);
            return "register";
        }

        return "redirect:/login";
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
            @RequestParam(required = false) Boolean rememberMe,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        // Limpiar cualquier sesión previa antes de autenticar
        SecurityContextHolder.clearContext();
        request.getSession().invalidate();
        HttpSession session = request.getSession(true);

        try {
            // 1) Intentar credenciales de ADMIN
            if (adminValidator.isAdmin(username, password)) {
                UserDetails adminUser = org.springframework.security.core.userdetails.User
                        .withUsername(username)
                        .password("")
                        .authorities("ADMIN")
                        .build();

                Authentication auth = new UsernamePasswordAuthenticationToken(
                        adminUser, null, adminUser.getAuthorities());
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(auth);
                SecurityContextHolder.setContext(context);
                session.setAttribute("SPRING_SECURITY_CONTEXT", context);

                // Configurar “rememberMe” (7 días) o “hasta que cierre navegador” (-1)
                if (Boolean.TRUE.equals(rememberMe)) {
                    session.setMaxInactiveInterval(7 * 24 * 60 * 60);
                } else {
                    session.setMaxInactiveInterval(-1);
                }
                return "redirect:/admin/dashboard";
            }

            // 2) Intentar credenciales de usuario en Firebase
            try {
                boolean ok = authService.loginUser(username, password);
                if (ok) {
                    UserDetails normalUser = org.springframework.security.core.userdetails.User
                            .withUsername(username)    // el email será el username
                            .password("")             // no se usa aquí
                            .authorities("USER")
                            .build();
                    Authentication auth = new UsernamePasswordAuthenticationToken(
                            normalUser, null, normalUser.getAuthorities());

                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(auth);
                    SecurityContextHolder.setContext(context);
                    session.setAttribute("SPRING_SECURITY_CONTEXT", context);
                    session.setAttribute("userRole", "USER");
                    session.setAttribute("userEmail", username);

                    if (Boolean.TRUE.equals(rememberMe)) {
                        session.setMaxInactiveInterval(7 * 24 * 60 * 60);
                    } else {
                        session.setMaxInactiveInterval(-1);
                    }
                    return "redirect:/";
                }
                // Teóricamente no llegas aquí porque loginUser lanza excepción si falla
                redirectAttributes.addFlashAttribute("loginError", "No se pudo autenticar en Firebase");
                return "redirect:/login";

            } catch (FirebaseRestLogin.FirebaseRestLoginException ex) {
                String code = ex.getMessage();
                switch (code) {
                    case "INVALID_PASSWORD":
                        redirectAttributes.addFlashAttribute("loginError", "Contraseña incorrecta.");
                        break;
                    case "EMAIL_NOT_FOUND":
                        redirectAttributes.addFlashAttribute("loginError", "No existe usuario con ese correo.");
                        break;
                    case "USER_DISABLED":
                        redirectAttributes.addFlashAttribute("loginError", "Usuario deshabilitado.");
                        break;
                    default:
                        redirectAttributes.addFlashAttribute("loginError", "Error de autenticación: " + code);
                }
                return "redirect:/login";
            }

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("loginError", "Error inesperado al iniciar sesión.");
            return "redirect:/login";
        }
    }

}