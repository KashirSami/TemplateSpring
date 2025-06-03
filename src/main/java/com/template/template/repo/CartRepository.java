package com.template.template.repo;

import com.template.template.model.CartItem;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public interface CartRepository {
    void saveCart(String userId, List<CartItem> items);
    List<CartItem> getCartItems(String userId);

    List<CartItem> getCart(String userId) throws ExecutionException, InterruptedException;

    void clearCart(String userId);
    void addToCart(String userId, String productId, int cantidad);
}
