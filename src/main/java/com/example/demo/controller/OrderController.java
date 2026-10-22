package com.example.demo.controller;

import com.example.demo.model.ClickTransaction;
import com.example.demo.model.Order;
import com.example.demo.service.ClickService;
import com.example.demo.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;
    private final ClickService clickService;

    public OrderController(OrderService orderService, ClickService clickService) {
        this.orderService = orderService;
        this.clickService = clickService;
    }

    @GetMapping("/orders")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        return orderService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/orders")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> body) {
        String title = (String) body.getOrDefault("title", "Yangi buyurtma");
        BigDecimal amount = new BigDecimal(body.get("amount").toString());

        Order order = orderService.createOrder(title, amount);
        String payUrl = orderService.buildPayUrl(order.getId(), null);

        return ResponseEntity.ok(Map.of(
                "order", order,
                "payUrl", payUrl
        ));
    }

    @GetMapping("/orders/{id}/pay-url")
    public ResponseEntity<Map<String, String>> getPayUrl(@PathVariable Long id,
                                                         @RequestParam(required = false) String returnUrl) {
        try {
            String payUrl = orderService.buildPayUrl(id, returnUrl);
            return ResponseEntity.ok(Map.of("payUrl", payUrl));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/transactions")
    public List<ClickTransaction> getTransactions() {
        return clickService.getAllTransactions();
    }
}
