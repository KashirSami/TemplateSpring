package com.template.template.controller;

import com.template.template.model.Product;
import com.template.template.service.FirestoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private FirestoreService firestoreService;

    @GetMapping
    public String testFirestore() throws ExecutionException, InterruptedException {
        try {
            List<Product> products = firestoreService.getAllProducts();
            return "Conexión exitosa. Productos encontrados: " + products.size();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}