package com.template.template.service;

import com.template.template.model.CartItem;
import com.template.template.model.Product;
import com.template.template.repo.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class CartService {

    @Qualifier("cartRepository" )
    @Autowired
    private CartRepository cartRepo;
    @Autowired private FirebaseStorageService productService; // para consultar datos de producto

    public void addItem(String userId, String productId, int cantidad) throws Exception {
        List<CartItem> current = cartRepo.getCart(userId);
        Optional<CartItem> existente = current.stream()
                .filter(ci -> ci.getProductId().equals(productId))
                .findFirst();
        if (existente.isPresent()) {
            existente.get().setQuantity(existente.get().getQuantity() + cantidad);
        } else {
            // Buscar el producto por ID para obtener nombre y precio
            Product p = productService.getProductById(productId);
            CartItem ci = new CartItem();
            ci.setProductId(productId);
            ci.setItemName(p.getNombre());
            ci.setUnitPrice(p.getPrecio());
            ci.setQuantity(cantidad);
            current.add(ci);
        }
        cartRepo.saveCart(userId, current);
    }

    public List<CartItem> getCart(String userId) throws ExecutionException, InterruptedException {
        return cartRepo.getCart(userId);
    }

    public void clearCart(String userId) {
        cartRepo.clearCart(userId);
    }

    public void updateItemQuantity(String userId, String productId, int cantidad) throws Exception {
        cartRepo.updateQuantity(userId, productId, cantidad);
    }
    public double calculateTotal(List<CartItem> items) {
        return items.stream()
                .mapToDouble(i -> i.getUnitPrice() * i.getQuantity())
                .sum();
    }

}

