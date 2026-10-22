package com.example.demo.service;

import com.example.demo.dto.CardConfirmOtpRequest;
import com.example.demo.dto.CardVerifyRequest;
import com.example.demo.dto.CardVerifyResponse;
import com.example.demo.model.Order;
import com.example.demo.model.PaymentMethod;
import com.example.demo.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CardService {

    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final Map<Long, OtpEntry> otpStorage = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public CardService(OrderRepository orderRepository, OrderService orderService) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    private static class OtpEntry {
        String code;
        String maskedPan;
        LocalDateTime expiresAt;

        OtpEntry(String code, String maskedPan, LocalDateTime expiresAt) {
            this.code = code;
            this.maskedPan = maskedPan;
            this.expiresAt = expiresAt;
        }
    }

    /**
     * Karta ma'lumotlarini tekshirish va SMS kod yuborish
     */
    public CardVerifyResponse verifyAndSendOtp(CardVerifyRequest req) {
        String pan = sanitizePan(req.getCardNumber());
        String expire = req.getExpireDate() != null ? req.getExpireDate().trim() : "";

        // 1. Karta raqamini tekshirish
        if (pan == null || pan.length() != 16) {
            return CardVerifyResponse.fail("Karta raqami 16 ta raqamdan iborat bo'lishi kerak!");
        }

        // Luhn algoritmi bilan tekshirish
        if (!isValidLuhn(pan)) {
            return CardVerifyResponse.fail("Karta raqami noto'g'ri kiritilgan (Luhn tekshiruvidan o'tmadi)!");
        }

        // Karta turini aniqlash
        String cardType = detectCardType(pan);
        if ("UNKNOWN".equals(cardType)) {
            return CardVerifyResponse.fail("Noma'lum karta turi! Faqat Uzcard (8600), Humo (9860), Visa (4...) va Mastercard qabul qilinadi.");
        }

        // 2. Amal qilish muddatini tekshirish (MM/YY)
        String expireError = validateExpireDate(expire);
        if (expireError != null) {
            return CardVerifyResponse.fail(expireError);
        }

        // 3. Masked PAN yaratish
        String maskedPan = pan.substring(0, 4) + " •••• •••• " + pan.substring(12);

        // 4. SMS OTP generatsiya qilish
        String otpCode = String.format("%06d", 100000 + random.nextInt(900000));
        Long orderId = req.getOrderId() != null ? req.getOrderId() : 0L;

        otpStorage.put(orderId, new OtpEntry(otpCode, maskedPan, LocalDateTime.now().plusMinutes(5)));

        return CardVerifyResponse.success(cardType, maskedPan, otpCode);
    }

    /**
     * SMS OTP kodni tasdiqlab to'lovni amalga oshirish
     */
    @Transactional
    public Map<String, Object> confirmOtp(CardConfirmOtpRequest req) {
        Long orderId = req.getOrderId();
        OtpEntry entry = otpStorage.get(orderId);

        if (entry == null) {
            return Map.of("success", false, "message", "SMS kodi so'ralmagan yoki muddati tugagan!");
        }

        if (LocalDateTime.now().isAfter(entry.expiresAt)) {
            otpStorage.remove(orderId);
            return Map.of("success", false, "message", "SMS kodi muddati o'tib ketgan, qaytadan so'rang!");
        }

        if (!entry.code.equals(req.getOtpCode().trim())) {
            return Map.of("success", false, "message", "SMS tasdiqlash kodi noto'g'ri kiritildi!");
        }

        // Kod to'g'ri! Buyurtmani PAID qilish
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setCardPanMasked(entry.maskedPan);
            order.setPaymentMethod(PaymentMethod.CARD);
            orderRepository.save(order);
            orderService.markPaid(orderId);
        }

        otpStorage.remove(orderId);
        return Map.of("success", true, "message", "To'lov muvaffaqiyatli amalga oshirildi!", "orderId", orderId);
    }

    public static String detectCardType(String pan) {
        if (pan == null || pan.length() < 4) return "UNKNOWN";
        if (pan.startsWith("8600") || pan.startsWith("5614")) return "UZCARD";
        if (pan.startsWith("9860")) return "HUMO";
        if (pan.startsWith("4")) return "VISA";
        if (pan.startsWith("51") || pan.startsWith("52") || pan.startsWith("53")
                || pan.startsWith("54") || pan.startsWith("55") || pan.startsWith("22")
                || pan.startsWith("23") || pan.startsWith("24") || pan.startsWith("25")
                || pan.startsWith("26") || pan.startsWith("27")) {
            return "MASTERCARD";
        }
        return "UNKNOWN";
    }

    public static boolean isValidLuhn(String pan) {
        int sum = 0;
        boolean alternate = false;
        for (int i = pan.length() - 1; i >= 0; i--) {
            int n = pan.charAt(i) - '0';
            if (n < 0 || n > 9) return false;
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }

    public static String validateExpireDate(String expire) {
        if (expire == null || !expire.matches("^\\d{2}/\\d{2}$")) {
            return "Karta amal qilish muddati MM/YY formatida bo'lishi kerak (Masalan: 12/28)!";
        }

        try {
            int month = Integer.parseInt(expire.substring(0, 2));
            int year = 2000 + Integer.parseInt(expire.substring(3, 5));

            if (month < 1 || month > 12) {
                return "Oy 01 dan 12 gacha bo'lishi kerak!";
            }

            LocalDate now = LocalDate.now();
            int currentYear = now.getYear();
            int currentMonth = now.getMonthValue();

            if (year < currentYear || (year == currentYear && month < currentMonth)) {
                return "Karta amal qilish muddati tugagan!";
            }

            if (year > currentYear + 15) {
                return "Karta amal qilish yili noto'g'ri kiritilgan!";
            }

            return null; // Hammasi to'g'ri
        } catch (Exception e) {
            return "Karta amal qilish muddati noto'g'ri!";
        }
    }

    private String sanitizePan(String raw) {
        if (raw == null) return null;
        return raw.replaceAll("[^0-9]", "");
    }
}
