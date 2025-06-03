package com.template.template.implement;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.template.template.model.Order;
import com.template.template.model.OrderProduct;
import com.template.template.repo.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class FirestoreOrderRepository implements OrderRepository {

    private static final String COLLECTION = "orders";
    @Autowired
    private Firestore firestore;

    @Override
    public String saveOrder(Order order) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", order.getUserId());
        data.put("products", order.getProducts());
        data.put("totalPaid", order.getTotal());
        data.put("timestamp", order.getTimestamp());
        data.put("status", order.getStatus());
        DocumentReference ref = firestore.collection(COLLECTION).document();
        ref.set(data);
        return ref.getId();
    }

    @Override
    public List<Order> getOrdersByUser(String userId) throws ExecutionException, InterruptedException {
        QuerySnapshot snaps = firestore.collection(COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().get();
        return snaps.getDocuments().stream().map(doc -> {
            Order order = new Order();
            order.setId(doc.getId());
            order.setUserId((String) doc.get("userId"));
            order.setTotal(((Double) doc.get("totalPaid")));
            order.setTimestamp(doc.getDate("timestamp"));
            order.setStatus((String) doc.get("status"));
            // Convertir lista genérica a List<OrderProduct>
            List<Map<String,Object>> rawProducts = (List<Map<String,Object>>) doc.get("products");
            List<OrderProduct> listaProductos = rawProducts.stream().map(map -> {
                OrderProduct op = new OrderProduct();
                op.setProductId((String) map.get("productId"));
                op.setNombre((String) map.get("nombre"));
                op.setPrecioUnitario((Double) map.get("precioUnitario"));
                op.setCantidad((Integer)map.get("cantidad"));
                return op;
            }).collect(Collectors.toList());
            order.setProducts(listaProductos);
            return order;
        }).collect(Collectors.toList());
    }

    @Override
    public Order getOrderById(String orderId) throws ExecutionException, InterruptedException {
        return null;
    }
}