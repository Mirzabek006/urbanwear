package com.example.demo;

import com.example.demo.config.ClickConfig;
import com.example.demo.dto.ClickErrorCode;
import com.example.demo.dto.ClickRequest;
import com.example.demo.dto.ClickResponse;
import com.example.demo.model.Order;
import com.example.demo.model.OrderStatus;
import com.example.demo.repository.OrderRepository;
import com.example.demo.service.ClickService;
import com.example.demo.service.OrderService;
import com.example.demo.util.ClickUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ClickIntegrationTests {

    @Autowired
    private ClickService clickService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ClickConfig clickConfig;

    private Order testOrder;
    private final String signTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    @BeforeEach
    void setUp() {
        testOrder = orderService.createOrder("Test Mahsulot", new BigDecimal("50000.00"));
    }

    @Test
    @DisplayName("1. Muvaffaqiyatli Prepare so'rovi (error = 0)")
    void testSuccessfulPrepare() {
        Long clickTransId = 123456789L;
        String amount = testOrder.getAmount().toPlainString();

        String sign = ClickUtil.generatePrepareSign(
                clickTransId,
                clickConfig.getServiceId(),
                clickConfig.getSecretKey(),
                String.valueOf(testOrder.getId()),
                amount,
                0,
                signTime
        );

        ClickRequest req = new ClickRequest();
        req.setClickTransId(clickTransId);
        req.setServiceId(Integer.valueOf(clickConfig.getServiceId()));
        req.setMerchantTransId(String.valueOf(testOrder.getId()));
        req.setAmount(amount);
        req.setAction(0);
        req.setSignTime(signTime);
        req.setSignString(sign);

        ClickResponse res = clickService.processPrepare(req);

        assertNotNull(res);
        assertEquals(ClickErrorCode.SUCCESS, res.getError());
        assertEquals("Success", res.getErrorNote());
        assertEquals(testOrder.getId(), res.getMerchantPrepareId());

        Order updated = orderRepository.findById(testOrder.getId()).orElseThrow();
        assertEquals(OrderStatus.PREPARED, updated.getStatus());
        assertEquals(clickTransId, updated.getClickTransId());
    }

    @Test
    @DisplayName("2. Noto'g'ri MD5 sign bilan Prepare (error = -1)")
    void testPrepareInvalidSign() {
        ClickRequest req = new ClickRequest();
        req.setClickTransId(999999L);
        req.setServiceId(Integer.valueOf(clickConfig.getServiceId()));
        req.setMerchantTransId(String.valueOf(testOrder.getId()));
        req.setAmount(testOrder.getAmount().toPlainString());
        req.setAction(0);
        req.setSignTime(signTime);
        req.setSignString("TAMPERED_INVALID_MD5_SIGN");

        ClickResponse res = clickService.processPrepare(req);

        assertNotNull(res);
        assertEquals(ClickErrorCode.SIGN_CHECK_FAILED, res.getError());
    }

    @Test
    @DisplayName("3. Noto'g'ri summa bilan Prepare (error = -2)")
    void testPrepareIncorrectAmount() {
        Long clickTransId = 222222L;
        String wrongAmount = "99999.00";

        String sign = ClickUtil.generatePrepareSign(
                clickTransId,
                clickConfig.getServiceId(),
                clickConfig.getSecretKey(),
                String.valueOf(testOrder.getId()),
                wrongAmount,
                0,
                signTime
        );

        ClickRequest req = new ClickRequest();
        req.setClickTransId(clickTransId);
        req.setServiceId(Integer.valueOf(clickConfig.getServiceId()));
        req.setMerchantTransId(String.valueOf(testOrder.getId()));
        req.setAmount(wrongAmount);
        req.setAction(0);
        req.setSignTime(signTime);
        req.setSignString(sign);

        ClickResponse res = clickService.processPrepare(req);

        assertNotNull(res);
        assertEquals(ClickErrorCode.INCORRECT_AMOUNT, res.getError());
    }

    @Test
    @DisplayName("4. Mavjud bo'lmagan buyurtma uchun Prepare (error = -5)")
    void testPrepareOrderNotFound() {
        Long nonExistentId = 99999999L;
        Long clickTransId = 333333L;
        String amount = "50000.00";

        String sign = ClickUtil.generatePrepareSign(
                clickTransId,
                clickConfig.getServiceId(),
                clickConfig.getSecretKey(),
                String.valueOf(nonExistentId),
                amount,
                0,
                signTime
        );

        ClickRequest req = new ClickRequest();
        req.setClickTransId(clickTransId);
        req.setServiceId(Integer.valueOf(clickConfig.getServiceId()));
        req.setMerchantTransId(String.valueOf(nonExistentId));
        req.setAmount(amount);
        req.setAction(0);
        req.setSignTime(signTime);
        req.setSignString(sign);

        ClickResponse res = clickService.processPrepare(req);

        assertNotNull(res);
        assertEquals(ClickErrorCode.ORDER_NOT_FOUND, res.getError());
    }

    @Test
    @DisplayName("5. To'liq muvaffaqiyatli oqim: Prepare -> Complete (error = 0 va PAID)")
    void testFullPaymentFlow() {
        Long clickTransId = 7777777L;
        String amount = testOrder.getAmount().toPlainString();

        // 1. Prepare
        String prepSign = ClickUtil.generatePrepareSign(
                clickTransId,
                clickConfig.getServiceId(),
                clickConfig.getSecretKey(),
                String.valueOf(testOrder.getId()),
                amount,
                0,
                signTime
        );
        ClickRequest prepReq = new ClickRequest();
        prepReq.setClickTransId(clickTransId);
        prepReq.setServiceId(Integer.valueOf(clickConfig.getServiceId()));
        prepReq.setMerchantTransId(String.valueOf(testOrder.getId()));
        prepReq.setAmount(amount);
        prepReq.setAction(0);
        prepReq.setSignTime(signTime);
        prepReq.setSignString(prepSign);

        ClickResponse prepRes = clickService.processPrepare(prepReq);
        assertEquals(0, prepRes.getError());
        Long prepareId = prepRes.getMerchantPrepareId();

        // 2. Complete
        String compSign = ClickUtil.generateCompleteSign(
                clickTransId,
                clickConfig.getServiceId(),
                clickConfig.getSecretKey(),
                String.valueOf(testOrder.getId()),
                prepareId,
                amount,
                1,
                signTime
        );
        ClickRequest compReq = new ClickRequest();
        compReq.setClickTransId(clickTransId);
        compReq.setServiceId(Integer.valueOf(clickConfig.getServiceId()));
        compReq.setMerchantTransId(String.valueOf(testOrder.getId()));
        compReq.setMerchantPrepareId(prepareId);
        compReq.setAmount(amount);
        compReq.setAction(1);
        compReq.setError(0);
        compReq.setSignTime(signTime);
        compReq.setSignString(compSign);

        ClickResponse compRes = clickService.processComplete(compReq);
        assertEquals(0, compRes.getError());
        assertEquals("Success", compRes.getErrorNote());
        assertEquals(testOrder.getId(), compRes.getMerchantConfirmId());

        Order paidOrder = orderRepository.findById(testOrder.getId()).orElseThrow();
        assertEquals(OrderStatus.PAID, paidOrder.getStatus());
        assertNotNull(paidOrder.getPaidAt());

        // 3. Idempotentlik testi: Bir xil Complete so'rovi qayta kelsa xato bermasligi kerak
        ClickResponse retryRes = clickService.processComplete(compReq);
        assertEquals(0, retryRes.getError());
    }

    @Test
    @DisplayName("6. Click to'lovni bekor qilganda Complete (error < 0)")
    void testCompleteCancelledByClick() {
        Long clickTransId = 8888888L;
        String amount = testOrder.getAmount().toPlainString();

        String compSign = ClickUtil.generateCompleteSign(
                clickTransId,
                clickConfig.getServiceId(),
                clickConfig.getSecretKey(),
                String.valueOf(testOrder.getId()),
                testOrder.getId(),
                amount,
                1,
                signTime
        );

        ClickRequest compReq = new ClickRequest();
        compReq.setClickTransId(clickTransId);
        compReq.setServiceId(Integer.valueOf(clickConfig.getServiceId()));
        compReq.setMerchantTransId(String.valueOf(testOrder.getId()));
        compReq.setMerchantPrepareId(testOrder.getId());
        compReq.setAmount(amount);
        compReq.setAction(1);
        compReq.setError(-9); // Click tomonidan bekor qilingan
        compReq.setErrorNote("User cancelled in app");
        compReq.setSignTime(signTime);
        compReq.setSignString(compSign);

        ClickResponse res = clickService.processComplete(compReq);
        assertEquals(ClickErrorCode.TRANSACTION_CANCELLED, res.getError());

        Order cancelledOrder = orderRepository.findById(testOrder.getId()).orElseThrow();
        assertEquals(OrderStatus.CANCELLED, cancelledOrder.getStatus());
    }
}
