package com.template.template.controller;

import com.template.template.model.ProfileRequest;
import com.template.template.model.User;
import com.template.template.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profile")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping
    public String viewProfile(Model model) {
        User current = userService.getAuthenticatedUser();
        if (current == null) {
            return "redirect:/login";
        }

        // Rellenamos el ProfileRequest con los datos que ya existen
        ProfileRequest dto = new ProfileRequest();
        dto.setNombre(current.getNombre());
        dto.setTelefono(current.getTelefono());
        dto.setDireccion(current.getDireccion());

        model.addAttribute("profileRequest", dto);
        model.addAttribute("stripeCustomerId", current.getStripeCustomerId());
        return "profile";
    }

    /**
     * Procesar la actualización de datos de perfil (nombre, teléfono, dirección)
     */
    @PostMapping("/update")
    public String updateProfile(@ModelAttribute ProfileRequest profileRequest,
                                Model model) {
        User current = userService.getAuthenticatedUser();
        if (current == null) {
            return "redirect:/login";
        }

        // Actualizamos solo los campos que permitimos cambiar
        current.setNombre(profileRequest.getNombre());
        current.setTelefono(profileRequest.getTelefono());
        current.setDireccion(profileRequest.getDireccion());

        userService.save(current);
        return "redirect:/profile?success";
    }
}

