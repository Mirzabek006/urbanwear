package com.example.demo;

import com.example.demo.dto.CardConfirmOtpRequest;
import com.example.demo.dto.CardVerifyRequest;
import com.example.demo.dto.CardVerifyResponse;
import com.example.demo.model.Order;
import com.example.demo.model.OrderStatus;
import com.example.demo.model.PaymentMethod;
import com.example.demo.repository.OrderRepository;
import com.example.demo.service.CardService;
import com.example.demo.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CardServiceTests {

    @Autowired
    private CardService cardService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("Karta turini to'g'ri aniqlash")
    void testDetectCardType() {
        assertEquals("UZCARD", CardService.detectCardType("8600123456789012"));
        assertEquals("HUMO", CardService.detectCardType("9860123456789012"));
        assertEquals("VISA", CardService.detectCardType("4000123456789010"));
        assertEquals("MASTERCARD", CardService.detectCardType("5105105105105100"));
    }

    @Test
    @DisplayName("Amal qilish muddatini tekshirish")
    void testExpireDateValidation() {
        assertNull(CardService.validateExpireDate("12/29")); // Kelajak - to'g'ri
        assertNotNull(CardService.validateExpireDate("12/20")); // O'tgan - xato
        assertNotNull(CardService.validateExpireDate("15/28")); // Noto'g'ri oy - xato
        assertNotNull(CardService.validateExpireDate("abc")); // Noto'g'ri format
    }

    @Test
    @DisplayName("Luhn algoritmi tekshiruvi")
    void testLuhnCheck() {
        assertTrue(CardService.isValidLuhn("4532015112830366")); // Haqiqiy Visa test kartasi
        assertFalse(CardService.isValidLuhn("4532015112830367")); // Buzilgan oxirgi raqam
    }

    @Test
    @DisplayName("Karta orqali to'lov: Verify -> OTP -> Confirm -> PAID")
    void testCardPaymentFlow() {
        Order order = orderService.createOrder("Kurtka", new BigDecimal("650000.00"));

        CardVerifyRequest req = new CardVerifyRequest();
        req.setOrderId(order.getId());
        req.setCardNumber("4532015112830366");
        req.setExpireDate("12/28");

        CardVerifyResponse verifyRes = cardService.verifyAndSendOtp(req);
        assertTrue(verifyRes.isValid());
        assertEquals("VISA", verifyRes.getCardType());
        assertNotNull(verifyRes.getSimulatedOtpCode());

        // OTP tasdiqlash
        CardConfirmOtpRequest confirmReq = new CardConfirmOtpRequest();
        confirmReq.setOrderId(order.getId());
        confirmReq.setOtpCode(verifyRes.getSimulatedOtpCode());

        Map<String, Object> confirmRes = cardService.confirmOtp(confirmReq);
        assertEquals(true, confirmRes.get("success"));

        Order updated = orderRepository.findById(order.getId()).orElseThrow();
        assertEquals(OrderStatus.PAID, updated.getStatus());
        assertEquals(PaymentMethod.CARD, updated.getPaymentMethod());
        assertNotNull(updated.getCardPanMasked());
    }
}
