package com.template.template.repo;

import com.template.template.model.Order;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public interface OrderRepository {
    String saveOrder(Order order);  // devuelve ID generado por Firestore
    List<Order> getOrdersByUser(String userId) throws ExecutionException, InterruptedException;
    Order getOrderById(String orderId) throws ExecutionException, InterruptedException;
}
