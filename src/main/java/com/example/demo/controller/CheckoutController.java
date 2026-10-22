package com.example.demo.controller;

import com.example.demo.dto.CardConfirmOtpRequest;
import com.example.demo.dto.CardVerifyRequest;
import com.example.demo.dto.CardVerifyResponse;
import com.example.demo.dto.CheckoutRequest;
import com.example.demo.model.*;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.CardService;
import com.example.demo.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CheckoutController {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final CardService cardService;

    public CheckoutController(ProductRepository productRepository,
                              OrderRepository orderRepository,
                              OrderService orderService,
                              CardService cardService) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.cardService = cardService;
    }

    /**
     * Savatchadan buyurtmani rasmiylashtirish (Checkout)
     */
    @PostMapping("/checkout")
    @Transactional
    public ResponseEntity<Map<String, Object>> checkout(@RequestBody CheckoutRequest req) {
        if (req.getItems() == null || req.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Savatcha bo'sh!"));
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        StringBuilder titleBuilder = new StringBuilder();

        Order order = new Order();
        order.setStatus(OrderStatus.NEW);
        order.setPaymentMethod(req.getPaymentMethod() != null ? req.getPaymentMethod() : PaymentMethod.CLICK);
        order.setCustomerName(req.getCustomerName());
        order.setCustomerPhone(req.getCustomerPhone());
        order.setShippingAddress(req.getShippingAddress());

        for (CheckoutRequest.ItemDto itemDto : req.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Mahsulot topilmadi: " + itemDto.getProductId()));

            int qty = itemDto.getQuantity() != null && itemDto.getQuantity() > 0 ? itemDto.getQuantity() : 1;
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(qty));
            totalAmount = totalAmount.add(itemTotal);

            String sizeSuffix = (itemDto.getSelectedSize() != null && !itemDto.getSelectedSize().isEmpty())
                    ? " (" + itemDto.getSelectedSize() + ")" : "";

            if (titleBuilder.length() > 0) {
                titleBuilder.append(", ");
            }
            titleBuilder.append(product.getName()).append(sizeSuffix).append(" x").append(qty);

            OrderItem orderItem = new OrderItem(order, product.getId(), product.getName(), itemDto.getSelectedSize(), product.getPrice(), qty);
            order.addItem(orderItem);
        }

        order.setTitle(titleBuilder.toString());
        order.setAmount(totalAmount);

        // Agar naqd to'lov bo'lsa darhol qabul qilinadi
        if (order.getPaymentMethod() == PaymentMethod.CASH) {
            order.setStatus(OrderStatus.NEW);
        }

        Order savedOrder = orderRepository.save(order);

        Map<String, Object> response = new HashMap<>();
        response.put("order", savedOrder);
        response.put("paymentMethod", savedOrder.getPaymentMethod());

        // To'lov usuli bo'yicha qo'shimcha ma'lumotlar
        if (savedOrder.getPaymentMethod() == PaymentMethod.CLICK) {
            String clickUrl = orderService.buildPayUrl(savedOrder.getId(), null);
            response.put("clickUrl", clickUrl);
        } else if (savedOrder.getPaymentMethod() == PaymentMethod.PAYME) {
            // Payme Checkout URL shakllantirish (Base64)
            // m=MERCHANT_ID;ac.order_id=ORDER_ID;a=AMOUNT_IN_TIYIN
            long amountInTiyin = totalAmount.multiply(BigDecimal.valueOf(100)).longValue();
            String paymeParams = "m=64a1b2c3d4e5f67890123456;ac.order_id=" + savedOrder.getId() + ";a=" + amountInTiyin;
            String base64Params = Base64.getEncoder().encodeToString(paymeParams.getBytes(StandardCharsets.UTF_8));
            response.put("paymeUrl", "https://checkout.paycom.uz/" + base64Params);
        } else if (savedOrder.getPaymentMethod() == PaymentMethod.ALIF) {
            response.put("alifMessage", "Alif Nasiya arizangiz qabul qilindi. Tez orada operatorimiz bog'lanadi.");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Karta ma'lumotlarini tekshirish va SMS kod yuborish
     */
    @PostMapping("/card/verify")
    public ResponseEntity<CardVerifyResponse> verifyCard(@RequestBody CardVerifyRequest req) {
        CardVerifyResponse res = cardService.verifyAndSendOtp(req);
        return ResponseEntity.ok(res);
    }

    /**
     * SMS OTP kodni tasdiqlash va to'lovni yakunlash
     */
    @PostMapping("/card/confirm-otp")
    public ResponseEntity<Map<String, Object>> confirmOtp(@RequestBody CardConfirmOtpRequest req) {
        Map<String, Object> res = cardService.confirmOtp(req);
        return ResponseEntity.ok(res);
    }
}
