package com.example.demo.service;

import com.example.demo.config.ClickConfig;
import com.example.demo.dto.ClickErrorCode;
import com.example.demo.dto.ClickRequest;
import com.example.demo.dto.ClickResponse;
import com.example.demo.model.ClickTransaction;
import com.example.demo.model.Order;
import com.example.demo.model.OrderStatus;
import com.example.demo.repository.ClickTransactionRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.util.ClickUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ClickService {

    private static final Logger log = LoggerFactory.getLogger(ClickService.class);

    private final ClickConfig clickConfig;
    private final OrderRepository orderRepository;
    private final ClickTransactionRepository clickTransactionRepository;
    private final OrderService orderService;

    public ClickService(ClickConfig clickConfig,
                        OrderRepository orderRepository,
                        ClickTransactionRepository clickTransactionRepository,
                        OrderService orderService) {
        this.clickConfig = clickConfig;
        this.orderRepository = orderRepository;
        this.clickTransactionRepository = clickTransactionRepository;
        this.orderService = orderService;
    }

    /**
     * Prepare bosqichi (action = 0)
     */
    @Transactional
    public ClickResponse processPrepare(ClickRequest req) {
        log.info("[CLICK PREPARE] transId={}, merchantTransId={}, amount={}",
                req.getClickTransId(), req.getMerchantTransId(), req.getAmount());

        // 1. Imzoni tekshirish
        String expectedSign = ClickUtil.generatePrepareSign(
                req.getClickTransId(),
                clickConfig.getServiceId(),
                clickConfig.getSecretKey(),
                req.getMerchantTransId(),
                req.getAmount(),
                req.getAction(),
                req.getSignTime()
        );

        if (req.getSignString() == null || !expectedSign.equalsIgnoreCase(req.getSignString())) {
            log.warn("[CLICK PREPARE] Sign check failed! Expected={}, Got={}", expectedSign, req.getSignString());
            saveLog(req, ClickErrorCode.SIGN_CHECK_FAILED, "SIGN CHECK FAILED!");
            return ClickResponse.error(req.getClickTransId(), req.getMerchantTransId(),
                    ClickErrorCode.SIGN_CHECK_FAILED, "SIGN CHECK FAILED!");
        }

        // 2. Buyurtmani tekshirish
        Long orderId = parseOrderId(req.getMerchantTransId());
        if (orderId == null) {
            saveLog(req, ClickErrorCode.ORDER_NOT_FOUND, "Invalid order id format");
            return ClickResponse.error(req.getClickTransId(), req.getMerchantTransId(),
                    ClickErrorCode.ORDER_NOT_FOUND, "User does not exist");
        }

        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            saveLog(req, ClickErrorCode.ORDER_NOT_FOUND, "Order not found: " + orderId);
            return ClickResponse.error(req.getClickTransId(), req.getMerchantTransId(),
                    ClickErrorCode.ORDER_NOT_FOUND, "User does not exist");
        }

        Order order = orderOpt.get();

        // 3. Buyurtma holatini tekshirish
        if (order.isPaid()) {
            saveLog(req, ClickErrorCode.ALREADY_PAID, "Order already paid");
            return ClickResponse.error(req.getClickTransId(), req.getMerchantTransId(),
                    ClickErrorCode.ALREADY_PAID, "Already paid");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            saveLog(req, ClickErrorCode.TRANSACTION_CANCELLED, "Order was cancelled");
            return ClickResponse.error(req.getClickTransId(), req.getMerchantTransId(),
                    ClickErrorCode.TRANSACTION_CANCELLED, "Transaction cancelled");
        }

        // 4. Summani tekshirish
        try {
            BigDecimal reqAmount = new BigDecimal(req.getAmount());
            if (order.getAmount().compareTo(reqAmount) != 0) {
                log.warn("[CLICK PREPARE] Incorrect amount! Order={}, Req={}", order.getAmount(), reqAmount);
                saveLog(req, ClickErrorCode.INCORRECT_AMOUNT, "Incorrect amount");
                return ClickResponse.error(req.getClickTransId(), req.getMerchantTransId(),
                        ClickErrorCode.INCORRECT_AMOUNT, "Incorrect parameter amount");
            }
        } catch (Exception e) {
            saveLog(req, ClickErrorCode.INCORRECT_AMOUNT, "Cannot parse amount: " + req.getAmount());
            return ClickResponse.error(req.getClickTransId(), req.getMerchantTransId(),
                    ClickErrorCode.INCORRECT_AMOUNT, "Incorrect parameter amount");
        }

        // 5. Buyurtmani PREPARED qilish va clickTransId ni saqlash
        orderService.markPrepared(order.getId(), req.getClickTransId());
        saveLog(req, ClickErrorCode.SUCCESS, "Prepare success");

        // 6. Muvaffaqiyatli javob (merchant_prepare_id qaytariladi)
        return ClickResponse.successPrepare(req.getClickTransId(), req.getMerchantTransId(), order.getId());
    }

    /**
     * Complete bosqichi (action = 1)
     */
    @Transactional
    public ClickResponse processComplete(ClickRequest req) {
        log.info("[CLICK COMPLETE] transId={}, merchantTransId={}, prepareId={}, error={}",
                req.getClickTransId(), req.getMerchantTransId(), req.getMerchantPrepareId(), req.getError());

        // 1. Imzoni tekshirish
        String expectedSign = ClickUtil.generateCompleteSign(
                req.getClickTransId(),
                clickConfig.getServiceId(),
                clickConfig.getSecretKey(),
                req.getMerchantTransId(),
                req.getMerchantPrepareId(),
                req.getAmount(),
                req.getAction(),
                req.getSignTime()
        );

        if (req.getSignString() == null || !expectedSign.equalsIgnoreCase(req.getSignString())) {
            log.warn("[CLICK COMPLETE] Sign check failed! Expected={}, Got={}", expectedSign, req.getSignString());
            saveLog(req, ClickErrorCode.SIGN_CHECK_FAILED, "SIGN CHECK FAILED!");
            return ClickResponse.error(req.getClickTransId(), req.getMerchantTransId(),
                    ClickErrorCode.SIGN_CHECK_FAILED, "SIGN CHECK FAILED!");
        }

        Long orderId = parseOrderId(req.getMerchantTransId());
        if (orderId == null) {
            saveLog(req, ClickErrorCode.TRANSACTION_NOT_FOUND, "Invalid order id format");
            return ClickResponse.error(req.getClickTransId(), req.getMerchantTransId(),
                    ClickErrorCode.TRANSACTION_NOT_FOUND, "Transaction does not exist");
        }

        // 2. Click tomonidan bekor qilinganini tekshirish (error < 0)
        if (req.getError() != null && req.getError() < 0) {
            log.warn("[CLICK COMPLETE] Click reported error: {} - {}", req.getError(), req.getErrorNote());
            orderService.cancel(orderId);
            saveLog(req, ClickErrorCode.TRANSACTION_CANCELLED, "Cancelled by Click: " + req.getErrorNote());
            return ClickResponse.error(req.getClickTransId(), req.getMerchantTransId(),
                    ClickErrorCode.TRANSACTION_CANCELLED, "Transaction cancelled");
        }

        // 3. Buyurtmani topish
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            saveLog(req, ClickErrorCode.TRANSACTION_NOT_FOUND, "Order not found: " + orderId);
            return ClickResponse.error(req.getClickTransId(), req.getMerchantTransId(),
                    ClickErrorCode.TRANSACTION_NOT_FOUND, "Transaction does not exist");
        }

        Order order = orderOpt.get();

        // 4. Idempotentlik tekshiruvi (takroriy so'rov)
        if (order.isPaid()) {
            if (order.getClickTransId() != null && order.getClickTransId().equals(req.getClickTransId())) {
                log.info("[CLICK COMPLETE] Idempotent request for already paid order {}", order.getId());
                return ClickResponse.successComplete(req.getClickTransId(), req.getMerchantTransId(), order.getId());
            } else {
                saveLog(req, ClickErrorCode.ALREADY_PAID, "Already paid under another trans");
                return ClickResponse.error(req.getClickTransId(), req.getMerchantTransId(),
                        ClickErrorCode.ALREADY_PAID, "Already paid");
            }
        }

        // 5. Summani tekshirish
        try {
            BigDecimal reqAmount = new BigDecimal(req.getAmount());
            if (order.getAmount().compareTo(reqAmount) != 0) {
                saveLog(req, ClickErrorCode.INCORRECT_AMOUNT, "Incorrect amount in complete");
                return ClickResponse.error(req.getClickTransId(), req.getMerchantTransId(),
                        ClickErrorCode.INCORRECT_AMOUNT, "Incorrect parameter amount");
            }
        } catch (Exception e) {
            saveLog(req, ClickErrorCode.INCORRECT_AMOUNT, "Cannot parse amount");
            return ClickResponse.error(req.getClickTransId(), req.getMerchantTransId(),
                    ClickErrorCode.INCORRECT_AMOUNT, "Incorrect parameter amount");
        }

        // 6. To'lovni tasdiqlash
        orderService.markPaid(order.getId());
        saveLog(req, ClickErrorCode.SUCCESS, "Complete success");

        // 7. Muvaffaqiyatli javob (merchant_confirm_id qaytariladi)
        return ClickResponse.successComplete(req.getClickTransId(), req.getMerchantTransId(), order.getId());
    }

    /**
     * Bitta endpoint orqali ikkala harakatni (prepare yoki complete) qabul qilish
     */
    @Transactional
    public ClickResponse process(ClickRequest req) {
        if (req.getAction() == null) {
            return ClickResponse.error(ClickErrorCode.ACTION_NOT_FOUND, "Action not found");
        }
        if (req.getAction() == 0) {
            return processPrepare(req);
        } else if (req.getAction() == 1) {
            return processComplete(req);
        } else {
            return ClickResponse.error(ClickErrorCode.ACTION_NOT_FOUND, "Unknown action: " + req.getAction());
        }
    }

    public List<ClickTransaction> getAllTransactions() {
        return clickTransactionRepository.findAllByOrderByIdDesc();
    }

    private void saveLog(ClickRequest req, int status, String note) {
        try {
            BigDecimal amt = null;
            if (req.getAmount() != null) {
                try { amt = new BigDecimal(req.getAmount()); } catch (Exception ignored) {}
            }
            ClickTransaction tx = new ClickTransaction(
                    req.getClickTransId(),
                    req.getClickPaydocId(),
                    parseOrderId(req.getMerchantTransId()),
                    amt,
                    req.getAction(),
                    status,
                    req.getSignTime(),
                    req.getSignString(),
                    note
            );
            clickTransactionRepository.save(tx);
        } catch (Exception e) {
            log.error("Failed to save ClickTransaction log", e);
        }
    }

    private Long parseOrderId(String str) {
        if (str == null || str.trim().isEmpty()) return null;
        try {
            return Long.parseLong(str.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
