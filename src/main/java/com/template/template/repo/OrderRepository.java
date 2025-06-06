package com.template.template.repo;

import com.template.template.model.Order;
import java.util.List;
import java.util.concurrent.ExecutionException;


public interface OrderRepository {
    String saveOrder(Order order);  // devuelve ID generado por Firestore
    List<Order> getOrdersByUser(String userId) throws ExecutionException, InterruptedException;
    Order getOrderById(String orderId) throws ExecutionException, InterruptedException;
}
