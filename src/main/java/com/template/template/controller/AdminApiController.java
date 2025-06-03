package com.template.template.controller;

import com.template.template.model.Product;
import com.template.template.service.FirebaseStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/api/products")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminApiController {

    @Autowired
    private FirebaseStorageService storageService;

    @GetMapping
    public List<Product> listAll(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category
    ) throws Exception {
        if (query != null && !query.isBlank() && category != null && !category.isBlank()) {
            return storageService.searchByNameAndCategory(query, category);
        } else if (query != null && !query.isBlank()) {
            return storageService.searchProducts(query);
        } else if (category != null && !category.isBlank()  && !"all".equalsIgnoreCase(category)) {
            return storageService.filterByCategory(category);
        } else {
            return storageService.getAllProducts();
        }
    }
}
