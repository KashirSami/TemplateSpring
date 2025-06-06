package com.template.template.service;

import com.template.template.model.CartItem;
import com.template.template.model.Order;
import com.template.template.model.OrderProduct;
import com.template.template.repo.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private CartService cartService;
    @Qualifier("firestoreOrderRepository")
    @Autowired
    private OrderRepository orderRepo;

    public String createOrder(String userId) throws ExecutionException, InterruptedException {
        List<CartItem> cartItems = cartService.getCart(userId);
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("No hay productos en el carrito");
        }

        // Transformar CartItem a OrderProduct
        List<OrderProduct> products = cartItems.stream().map(ci -> {
            OrderProduct op = new OrderProduct();
            op.setProductId(ci.getProductId());
            op.setNombre(ci.getItemName());
            op.setPrecioUnitario(ci.getUnitPrice());
            op.setCantidad(ci.getQuantity());
            return op;
        }).collect(Collectors.toList());

        double total = cartService.calculateTotal(cartItems);
        Order order = new Order();
        order.setUserId(userId);
        order.setProduct(products);
        order.setTotal(total);
        order.setTimestamp(new Date());
        order.setStatus("COMPLETADO");

        String orderId = orderRepo.saveOrder(order);
        cartService.clearCart(userId);
        return orderId;
    }

    public List<Order> getOrdersForUser(String userId) throws ExecutionException, InterruptedException {
        return orderRepo.getOrdersByUser(userId);
    }
    public Order getOrderById(String orderId) throws ExecutionException, InterruptedException {
        return orderRepo.getOrderById(orderId);
    }
}
