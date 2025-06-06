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

@Repository("cartRepository")
public class FireStoreCartRepository implements CartRepository {
    private static final String COLLECTION = "cart";
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
        try {
            return getCart(userId);
        } catch (Exception e) {
            return new ArrayList<>();

        }
    }

    @Override
    public List<CartItem> getCart(String userId) throws ExecutionException, InterruptedException {
        DocumentSnapshot snap = firestore.collection(COLLECTION)
                .document(userId)
                .get().get();
        if (!snap.exists()) {
            return new ArrayList<>();
        }
        List<Map<String, Object>> rawItems = (List<Map<String, Object>>) snap.get("items");
        if (rawItems == null) return new ArrayList<>();
        return rawItems.stream().map(map -> {
            CartItem ci = new CartItem();
            ci.setProductId((String) map.get("productId"));
            ci.setItemName((String) map.get("itemName"));
            Object price = map.get("precioUnitario");
            if (price instanceof Number) {
                ci.setUnitPrice(((Number) price).doubleValue());
            }
            Object qty = map.get("cantidad");
            if (qty instanceof Number) {
                ci.setQuantity(((Number) qty).intValue());
            }
            return ci;
        }).collect(Collectors.toList());
    }

    @Override
    public void clearCart(String userId) {
        firestore.collection(COLLECTION).document(userId).delete();
    }

    @Override
    public void addToCart(String userId, String productId, int cantidad) {
        try {
            List<CartItem> current = getCart(userId);
            Optional<CartItem> existente = current.stream()
                    .filter(ci -> ci.getProductId().equals(productId))
                    .findFirst();
            if (existente.isPresent()) {
                CartItem ci = existente.get();
                ci.setQuantity(ci.getQuantity() + cantidad);
            } else {
                CartItem ci = new CartItem();
                ci.setProductId(productId);
                ci.setQuantity(cantidad);
                current.add(ci);
            }
            saveCart(userId, current);
        } catch (Exception e) {
        }
    }
}
