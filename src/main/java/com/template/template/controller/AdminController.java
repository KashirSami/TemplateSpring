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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {


    private final FirebaseStorageService storageService;
    private final ExcelService excelService;
    private final AdminService adminService;

    @Autowired
    public AdminController(FirebaseStorageService storageService,
                           ExcelService excelService,
                           AdminService adminService) {
        this.storageService = storageService;
        this.excelService  = excelService;
        this.adminService  = adminService;
    }

    // 1. Mostrar dashboard con todos los productos
    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(value="success", required=false) String success,
                            @RequestParam(value="updated", required=false) String updated,
                            Model model) throws Exception {
        List<Product> all = storageService.getAllProducts();
        model.addAttribute("products", all);
        if (success!=null)  model.addAttribute("msg","Importación completada");
        if (updated!=null)  model.addAttribute("msg","Producto actualizado");
        return "admin/dashboard";
    }

    // 2. Importar Excel
    @PostMapping("/dashboard/upload")
    public String uploadExcel(@RequestParam("file") MultipartFile file,
                              RedirectAttributes attrs) throws IOException {
        excelService.processExcel(file);
        attrs.addAttribute("success","ok");
        return "redirect:/admin/dashboard?success";
    }

    // 3. Buscar / filtrar
    @GetMapping("/dashboard/search")
    public String search(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            Model model
    ) throws Exception {
        List<Product> results;
        if (query != null && !query.isBlank()) {
            results = storageService.searchProducts(query);
        } else if (category != null && !category.isBlank()) {
            results = storageService.filterByCategory(category);
        } else {
            results = storageService.getAllProducts();
        }
        model.addAttribute("productos", results);
        return "admin/dashboard";
    }

    // 4. Mostrar formulario de edición
    @GetMapping("/dashboard/edit/{id}")
    public String editForm(@PathVariable String id, Model model) throws Exception {
        Product p = storageService.getProductById(id);
        model.addAttribute("productos", p);
        return "admin/edit-product";
    }

    // 5. Guardar cambios
    @PostMapping("/dashboard/update")
    public String update(@ModelAttribute Product product,
                         RedirectAttributes attrs) throws Exception {
        storageService.updateProduct(product);
        attrs.addAttribute("updated","ok");
        return "redirect:/admin/dashboard?updated";
    }

    @GetMapping("/dashboard/filter")
    public String filterByCategory(@RequestParam String category, Model model) throws Exception {
        List<Product> filtrados = storageService.filterByCategory(category);
        model.addAttribute("productos", filtrados);
        return "admin/dashboard";
    }
}

