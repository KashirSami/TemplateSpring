package com.template.template.controller;

import com.google.api.client.util.Value;
import com.stripe.model.SetupIntent;
import com.stripe.param.SetupIntentCreateParams;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class StripeController {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @GetMapping("/stripe/create-setup-intent")
    public ResponseEntity<?> createSetupIntent(@RequestParam("customerId") String customerId) {

        try {
            com.stripe.Stripe.apiKey = stripeSecretKey;

            SetupIntentCreateParams params = SetupIntentCreateParams.builder()
                    .setCustomer(customerId)
                    .build();

            SetupIntent intent = SetupIntent.create(params);

            return ResponseEntity.ok(Map.of("clientSecret", intent.getClientSecret()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(Map.of("error", "No se pudo generar SetupIntent"));
        }
    }
}
