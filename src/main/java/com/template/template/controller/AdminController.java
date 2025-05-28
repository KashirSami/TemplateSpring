package com.template.template.controller;

import com.template.template.model.Product;
import com.template.template.service.AdminService;
import com.template.template.service.ExcelService;
import com.template.template.service.FirebaseStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private FirebaseStorageService storageService;

    @Autowired
    private ExcelService excelService;

    @Autowired
    private AdminService adminService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/dashboard")
    public String adminDashboard(Authentication authentication) {
        System.out.println("Roles del usuario: " + authentication.getAuthorities()); // Debe mostrar "[ADMIN]"
        return "admin/dashboard";
    }


    @PostMapping("/upload")
    public String uploadExcel(@RequestParam("file") MultipartFile file,
                              Authentication authentication) throws IOException {
        if (!adminService.isAdmin(authentication.getName())) {
            return "redirect:/access-denied";
        }

        excelService.processExcel(file);
        return "redirect:/dashboard?success";
    }

    @GetMapping("/search")
    public String searchProduct(@RequestParam String query, Model model) {
        List<Product> results = null;
        try {
            results = storageService.searchProducts(query);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        model.addAttribute("products", results);
        return "/search-results";
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable String id, @ModelAttribute Product product) {
        try {
            storageService.updateProduct(id, product);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/dashboard?updated";
    }
}
