package com.template.template.implement;

import com.google.cloud.firestore.*;
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

    private static final String COLLECTION = "order";
    @Autowired
    private Firestore firestore;

    @Override
    public String saveOrder(Order order) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", order.getUserId());
        data.put("products", order.getProduct());
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
            Object totalObj = doc.get("totalPaid");
            if (totalObj instanceof Number) {
                order.setTotal(((Number) totalObj).doubleValue());
            } else if (totalObj instanceof String) {
                try {
                    order.setTotal(Double.parseDouble((String) totalObj));
                } catch (NumberFormatException ignored) {
                }
            }
            order.setTimestamp(doc.getDate("timestamp"));
            order.setStatus((String) doc.get("status"));
            // Convertir lista genérica a List<OrderProduct>
            List<Map<String,Object>> rawProducts = (List<Map<String,Object>>) doc.get("productos");
            List<OrderProduct> listaProductos = rawProducts.stream().map(map -> {
                OrderProduct op = new OrderProduct();
                op.setProductId((String) map.get("id"));
                op.setNombre((String) map.get("nombre"));

                Object price = map.get("precioUnitario");
                if (price instanceof Number) {
                    op.setPrecioUnitario(((Number) price).doubleValue());
                } else if (price instanceof String) {
                    try {
                        op.setPrecioUnitario(Double.parseDouble((String) price));
                    } catch (NumberFormatException ignored) {
                    }
                }
                Object qty = map.get("cantidad");
                if (qty instanceof Number) {
                    op.setCantidad(((Number) qty).intValue());
                } else if (qty instanceof String) {
                    try {
                        op.setCantidad(Integer.parseInt((String) qty));
                    } catch (NumberFormatException ignored) {
                    }
                }

                return op;
            }).collect(Collectors.toList());
            order.setProduct(listaProductos);
            return order;
        }).collect(Collectors.toList());
    }

    @Override
    public Order getOrderById(String orderId) throws ExecutionException, InterruptedException {
        DocumentReference ref = firestore.collection(COLLECTION).document(orderId);
        DocumentSnapshot doc = ref.get().get();
        if (!doc.exists()) {
            return null;
        }

        Order order = new Order();
        order.setId(doc.getId());
        order.setUserId((String) doc.get("userId"));
        Object totalObj2 = doc.get("totalPaid");
        if (totalObj2 instanceof Number) {
            order.setTotal(((Number) totalObj2).doubleValue());
        } else if (totalObj2 instanceof String) {
            try {
                order.setTotal(Double.parseDouble((String) totalObj2));
            } catch (NumberFormatException ignored) {
            }
        }
        order.setTimestamp(doc.getDate("timestamp"));
        order.setStatus((String) doc.get("status"));

        List<Map<String, Object>> rawProducts = (List<Map<String, Object>>) doc.get("products");
        if (rawProducts != null) {
            List<OrderProduct> productos = rawProducts.stream().map(map -> {
                OrderProduct op = new OrderProduct();
                op.setProductId((String) map.get("productId"));
                op.setNombre((String) map.get("nombre"));
                Object price = map.get("precioUnitario");
                if (price instanceof Number) {
                    op.setPrecioUnitario(((Number) price).doubleValue());
                } else if (price instanceof String) {
                    try {
                        op.setPrecioUnitario(Double.parseDouble((String) price));
                    } catch (NumberFormatException ignored) {
                    }
                }
                Object qty = map.get("cantidad");
                if (qty instanceof Number) {
                    op.setCantidad(((Number) qty).intValue());
                } else if (qty instanceof String) {
                    try {
                        op.setCantidad(Integer.parseInt((String) qty));
                    } catch (NumberFormatException ignored) {
                    }
                }
                return op;
            }).collect(Collectors.toList());
            order.setProduct(productos);
        }
        return order;
    }
}