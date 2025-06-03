package com.template.template.controller;

import com.template.template.model.Order;
import com.template.template.model.User;
import com.template.template.service.OrderService;
import com.template.template.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired private UserService userService;

    @GetMapping("/history")
    public String viewOrderHistory(Model model) throws ExecutionException, InterruptedException {
        User user = userService.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }
        List<Order> orders = orderService.getOrdersForUser(user.getEmail());
        model.addAttribute("orders", orders);
        return "order-history"; // Thymeleaf
    }

    @GetMapping("/detail/{orderId}")
    public String viewOrderDetail(@PathVariable String orderId, Model model) throws ExecutionException, InterruptedException {
        User user = userService.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }
        // Podrías añadir un método orderService.getOrderById y validar que order.userId == user.getEmail()
        Order order = orderService.getOrderById(orderId);
        if (order == null || !order.getUserId().equals(user.getEmail())) {
            return "redirect:/orders/history";
        }
        model.addAttribute("order", order);
        return "order-detail"; // Thymeleaf
    }
}
