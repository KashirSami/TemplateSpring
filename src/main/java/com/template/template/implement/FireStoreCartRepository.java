package com.template.template.implement;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.template.template.model.CartItem;
import com.template.template.repo.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class FireStoreCartRepository implements CartRepository {
    private static final String COLLECTION = "carts";
    @Autowired
    private Firestore firestore;

    @Override
    public void saveCart(String userId, List<CartItem> items) {
        Map<String, Object> data = new HashMap<>();
        data.put("items", items);
        data.put("timestamp", new Date());
        firestore.collection(COLLECTION).document(userId)
                .set(data);
    }

    @Override
    public List<CartItem> getCartItems(String userId) {
        return List.of();
    }

    @Override
    public List<CartItem> getCart(String userId) throws ExecutionException, InterruptedException {
        DocumentSnapshot snap = firestore.collection(COLLECTION)
                .document(userId)
                .get().get();
        if (!snap.exists()) {
            return new ArrayList<>();
        }
        List<Map<String,Object>> rawItems = (List<Map<String,Object>>) snap.get("items");
        return rawItems.stream().map(map -> {
            CartItem ci = new CartItem();
            ci.setProductId((String) map.get("productId"));
            ci.setItemName((String) map.get("nombre"));
            ci.setUnitPrice((Double) map.get("precioUnitario"));
            ci.setQuantity((Integer) map.get("cantidad")); // Firestore guarda enteros como Long
            return ci;
        }).collect(Collectors.toList());
    }

    @Override
    public void clearCart(String userId) {
        firestore.collection(COLLECTION).document(userId).delete();
    }

    @Override
    public void addToCart(String userId, String productId, int cantidad) {

    }
}
