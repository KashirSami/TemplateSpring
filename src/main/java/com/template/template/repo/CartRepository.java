package com.template.template.repo;

import com.template.template.model.CartItem;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface CartRepository {
    void saveCart(String userId, List<CartItem> items);
    List<CartItem> getCartItems(String userId);
    List<CartItem> getCart(String userId) throws ExecutionException, InterruptedException;
    void clearCart(String userId);
    void addToCart(String userId, String productId, int cantidad);
    void updateQuantity(String userId, String productId, int cantidad) throws Exception;
}
