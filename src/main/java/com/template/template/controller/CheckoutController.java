package com.template.template.controller;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.template.template.model.CartItem;
import com.template.template.model.Order;
import com.template.template.model.User;
import com.template.template.service.CartService;
import com.template.template.service.OrderService;
import com.template.template.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Controller
public class CheckoutController {

    @Autowired
    private CartService cartService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;
    @Value("${stripe.public.key}")
    private String stripePublicKey;
    @Value("${url.check.success}")
    private String checkSuccessUrl;
    @Value("${url.checkout.cancel}")
    private String checkoutCancelUrl;

    // 1) Muestra la vista /checkout con los ítems del carrito
    @GetMapping("/checkout")
    public String viewCheckout(Model model) throws ExecutionException, InterruptedException {
        User user = userService.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }
        List<CartItem> items = cartService.getCart(user.getEmail());
        double total = cartService.calculateTotal(items);
        model.addAttribute("cartItems", items);
        model.addAttribute("subtotal", total);
        model.addAttribute("total", total);
        model.addAttribute("stripePublicKey", stripePublicKey);
        return "checkout";
    }

    // 2) Endpoint que crea la sesión de Checkout en Stripe y devuelve sessionId
    @PostMapping("/checkout/create-session")
    @ResponseBody
    public Map<String, String> createCheckoutSession() throws StripeException, ExecutionException, InterruptedException {
        User user = userService.getAuthenticatedUser();
        if (user == null) {
            throw new IllegalStateException("Usuario no autenticado");
        }
        List<CartItem> items = cartService.getCart(user.getEmail());
        Stripe.apiKey = stripeSecretKey;

        // Construcción de LineItems:
        List<SessionCreateParams.LineItem> stripeItems = items.stream().map(ci -> {
            return SessionCreateParams.LineItem.builder()
                    .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("eur")
                                    .setUnitAmount((long)(ci.getUnitPrice() * 100)) // en céntimos
                                    .setProductData(
                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                    .setName(ci.getItemName())
                                                    .build()
                                    )
                                    .build()
                    )
                    .setQuantity((long) ci.getQuantity())
                    .build();
        }).collect(Collectors.toList());

        SessionCreateParams params = SessionCreateParams.builder()
                .addAllLineItem(stripeItems)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(checkSuccessUrl)
                .setCancelUrl(checkoutCancelUrl)
                .build();

        Session session = Session.create(params.toMap());
        return Map.of("sessionId", session.getId());
    }

    //Página de éxito tras el pago
    @GetMapping("/checkout/success")
    public String checkoutSuccess(@RequestParam("session_id") String sessionId,
                                  Model model) throws StripeException, ExecutionException, InterruptedException {
        User user = userService.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }


        Stripe.apiKey = stripeSecretKey;
        Session stripeSession = Session.retrieve(sessionId);
        if (!"complete".equals(stripeSession.getPaymentStatus())) {
            return "redirect:/checkout";
        }


        //Crear la Order en Firestore
        Order order = orderService.createOrder(user.getEmail());

        //Pasar datos a la vista de confirmación
        model.addAttribute("orderId", order.getId());
        model.addAttribute("totalPaid", order.getTotal());
        return "checkout-success"; // Plantilla Thymeleaf
    }

    //Página si canceló el pago
    @GetMapping("/checkout/cancel")
    public String checkoutCancel() {
        return "checkout-cancel"; // Indica “Pago cancelado” y un link para volver al carrito
    }
}
