package com.template.template.controller;

import com.google.api.client.util.Value;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
import com.template.template.model.User;
import com.template.template.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.concurrent.ExecutionException;

@Controller
public class PaymentController {

    private final UserService userService;

    public PaymentController(UserService userService) {
        this.userService = userService;
    }

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${stripe.public.key}")
    private String stripePublicKey;

    @GetMapping("/profile/payment-method")
    public String showPaymentMethodPage(Model model) throws ExecutionException {
        User current = userService.getAuthenticatedUser();
        if (current == null) {
            return "redirect:/login";
        }

        // Si el usuario NO tiene aún stripeCustomerId, lo creamos ahora
        if (current.getStripeCustomerId() == null) {
            try {
                Stripe.apiKey = stripeSecretKey;
                CustomerCreateParams params = CustomerCreateParams.builder()
                        .setEmail(current.getEmail())
                        .setName(current.getNombre())
                        .setPhone(current.getTelefono())
                        .putMetadata("direccion", current.getDireccion() != null
                                ? current.getDireccion()
                                : "No especificada")
                        .build();
                Customer stripeCustomer = Customer.create(params);
                current.setStripeCustomerId(stripeCustomer.getId());
                userService.save(current);
            } catch (StripeException e) {
                model.addAttribute("error", "No se pudo crear el cliente Stripe. Intenta más tarde.");
                return "payment-method";
            }
        }

        // Enviamos a la vista la clave pública para Stripe.js
        model.addAttribute("stripePublicKey", stripePublicKey);
        model.addAttribute("stripeCustomerId", current.getStripeCustomerId());
        return "payment-method";
    }
}