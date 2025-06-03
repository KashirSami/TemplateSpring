package com.template.template.controller;

import com.template.template.model.Product;
import com.template.template.service.FirebaseStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductApiController {
    @Autowired
    private FirebaseStorageService storageService;

    @GetMapping
    public List<Product> all(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer limit
    ) throws Exception {
        List<Product> lista = storageService.getAllProducts();
        if (query != null && !query.isBlank()) {
            lista = storageService.searchProducts(query);
        } else if (category != null && !category.isBlank() && !"all".equalsIgnoreCase(category)) {
            lista = storageService.filterByCategory(category);
        }
        if (limit != null && lista.size() > limit) {
            return lista.subList(0, limit);
        }
        return lista;
    }

    @GetMapping("/{id}")
    public Product byId(@PathVariable String id) throws Exception {
        return storageService.getProductById(id);
    }
}
