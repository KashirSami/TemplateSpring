package com.template.template.controller;

import com.template.template.model.Product;
import com.template.template.service.FirebaseStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final FirebaseStorageService storageService;

    @Autowired
    public ProductController(FirebaseStorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("")
    public String products(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            Model model
    ) throws Exception {

        List<Product> lista;

        if (query != null && !query.isBlank()) {
            lista = storageService.searchProducts(query);
        } else if (category != null && !category.isBlank()) {
            lista = storageService.filterByCategory(category);
        } else {
            lista = storageService.getAllProducts();
        }

        model.addAttribute("products", lista);
        model.addAttribute("isAdmin", false);
        model.addAttribute("selectedQuery", query == null ? "" : query.trim());
        model.addAttribute("selectedCategory", category == null ? "" : category.trim());
        return "products";
    }
}


