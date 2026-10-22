package com.example.demo.service;

import com.example.demo.config.ClickConfig;
import com.example.demo.model.Order;
import com.example.demo.model.OrderStatus;
import com.example.demo.repository.OrderRepository;
import com.example.demo.util.ClickUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ClickConfig clickConfig;

    public OrderService(OrderRepository orderRepository, ClickConfig clickConfig) {
        this.orderRepository = orderRepository;
        this.clickConfig = clickConfig;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByIdDesc();
    }

    public Optional<Order> findById(Long id) {
        if (id == null) return Optional.empty();
        return orderRepository.findById(id);
    }

    @Transactional
    public Order createOrder(String title, BigDecimal amount) {
        Order order = new Order(title, amount);
        return orderRepository.save(order);
    }

    @Transactional
    public void markPrepared(Long orderId, Long clickTransId) {
        orderRepository.findById(orderId).ifPresent(order -> {
            if (order.getStatus() == OrderStatus.NEW) {
                order.setStatus(OrderStatus.PREPARED);
            }
            order.setClickTransId(clickTransId);
            orderRepository.save(order);
        });
    }

    @Transactional
    public void markPaid(Long orderId) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus(OrderStatus.PAID);
            order.setPaidAt(LocalDateTime.now());
            orderRepository.save(order);
        });
    }

    @Transactional
    public void cancel(Long orderId) {
        orderRepository.findById(orderId).ifPresent(order -> {
            if (order.getStatus() != OrderStatus.PAID) {
                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
            }
        });
    }

    public String buildPayUrl(Long orderId, String returnUrl) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Buyurtma topilmadi: " + orderId));

        if (returnUrl == null || returnUrl.trim().isEmpty()) {
            returnUrl = "http://localhost:8080/?orderId=" + orderId;
        }

        return ClickUtil.buildPaymentUrl(
                clickConfig.getCheckoutUrl(),
                clickConfig.getServiceId(),
                clickConfig.getMerchantId(),
                String.valueOf(order.getId()),
                order.getAmount().toPlainString(),
                returnUrl
        );
    }
}
