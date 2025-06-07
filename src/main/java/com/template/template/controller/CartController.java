package com.template.template.controller;

import com.template.template.model.AddToCartRequest;
import com.template.template.model.User;
import com.template.template.service.CartService;
import com.template.template.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;
import java.util.concurrent.ExecutionException;
import com.template.template.model.CartItem;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired private UserService userService;

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody AddToCartRequest req) throws Exception {
        User user = userService.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        cartService.addItem(user.getEmail(), req.getProductId(), req.getCantidad());
        return ResponseEntity.ok(Map.of("message", "Producto añadido al carrito"));
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart() throws ExecutionException, InterruptedException {
        User user = userService.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(cartService.getCart(user.getEmail()));
    }

    @PostMapping("/clear")
    public ResponseEntity<?> clearCart() throws ExecutionException {
        User user = userService.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        cartService.clearCart(user.getEmail());
        return ResponseEntity.ok(Map.of("message", "Carrito vaciado"));
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateQuantity(@RequestBody AddToCartRequest req) throws Exception {
        User user = userService.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        cartService.updateItemQuantity(user.getEmail(), req.getProductId(), req.getCantidad());
        return ResponseEntity.ok(Map.of("message", "Cantidad actualizada"));
    }
}
