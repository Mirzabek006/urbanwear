package com.example.demo.controller;

import com.example.demo.config.ClickConfig;
import com.example.demo.dto.ClickRequest;
import com.example.demo.dto.ClickResponse;
import com.example.demo.model.Order;
import com.example.demo.repository.OrderRepository;
import com.example.demo.service.ClickService;
import com.example.demo.util.ClickUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/simulator")
@CrossOrigin(origins = "*")
public class ClickSimulatorController {

    private final ClickConfig clickConfig;
    private final ClickService clickService;
    private final OrderRepository orderRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Random random = new Random();

    public ClickSimulatorController(ClickConfig clickConfig,
                                    ClickService clickService,
                                    OrderRepository orderRepository) {
        this.clickConfig = clickConfig;
        this.clickService = clickService;
        this.orderRepository = orderRepository;
    }

    /**
     * Click serveri o'rnidan PREPARE so'rovini yuborish
     */
    @PostMapping("/prepare")
    public ResponseEntity<Map<String, Object>> simulatePrepare(@RequestBody Map<String, Object> req) {
        Long orderId = Long.valueOf(req.get("orderId").toString());
        Order order = orderRepository.findById(orderId).orElse(null);

        String amount = req.containsKey("amount") && req.get("amount") != null
                ? req.get("amount").toString()
                : (order != null ? order.getAmount().toPlainString() : "10000");

        Long clickTransId = req.containsKey("clickTransId") && req.get("clickTransId") != null
                ? Long.valueOf(req.get("clickTransId").toString())
                : 1000000000L + random.nextInt(900000000);

        String signTime = LocalDateTime.now().format(FORMATTER);
        boolean tamperSign = Boolean.TRUE.equals(req.get("tamperSign"));

        String rawString = clickTransId + clickConfig.getServiceId() + clickConfig.getSecretKey()
                + orderId + amount + 0 + signTime;
        String signString = ClickUtil.md5(rawString);

        if (tamperSign) {
            signString = "INVALID_HASH_" + signString.substring(0, 10);
        }

        ClickRequest clickReq = new ClickRequest();
        clickReq.setClickTransId(clickTransId);
        clickReq.setServiceId(Integer.valueOf(clickConfig.getServiceId()));
        clickReq.setClickPaydocId(clickTransId + 10);
        clickReq.setMerchantTransId(String.valueOf(orderId));
        clickReq.setAmount(amount);
        clickReq.setAction(0);
        clickReq.setError(0);
        clickReq.setErrorNote("Success");
        clickReq.setSignTime(signTime);
        clickReq.setSignString(signString);

        ClickResponse response = clickService.processPrepare(clickReq);

        Map<String, Object> result = new HashMap<>();
        result.put("sentRequest", clickReq);
        result.put("rawSignFormula", "MD5(" + clickTransId + " + " + clickConfig.getServiceId() + " + SECRET_KEY + " + orderId + " + " + amount + " + 0 + " + signTime + ")");
        result.put("generatedSign", signString);
        result.put("receivedResponse", response);

        return ResponseEntity.ok(result);
    }

    /**
     * Click serveri o'rnidan COMPLETE so'rovini yuborish
     */
    @PostMapping("/complete")
    public ResponseEntity<Map<String, Object>> simulateComplete(@RequestBody Map<String, Object> req) {
        Long orderId = Long.valueOf(req.get("orderId").toString());
        Order order = orderRepository.findById(orderId).orElse(null);

        Long prepareId = req.containsKey("prepareId") && req.get("prepareId") != null
                ? Long.valueOf(req.get("prepareId").toString())
                : orderId;

        String amount = req.containsKey("amount") && req.get("amount") != null
                ? req.get("amount").toString()
                : (order != null ? order.getAmount().toPlainString() : "10000");

        Long clickTransId = req.containsKey("clickTransId") && req.get("clickTransId") != null
                ? Long.valueOf(req.get("clickTransId").toString())
                : (order != null && order.getClickTransId() != null ? order.getClickTransId() : 1000000001L);

        int clickError = req.containsKey("clickError") && req.get("clickError") != null
                ? Integer.parseInt(req.get("clickError").toString())
                : 0;

        String signTime = LocalDateTime.now().format(FORMATTER);
        boolean tamperSign = Boolean.TRUE.equals(req.get("tamperSign"));

        String rawString = clickTransId + clickConfig.getServiceId() + clickConfig.getSecretKey()
                + orderId + prepareId + amount + 1 + signTime;
        String signString = ClickUtil.md5(rawString);

        if (tamperSign) {
            signString = "INVALID_HASH_" + signString.substring(0, 10);
        }

        ClickRequest clickReq = new ClickRequest();
        clickReq.setClickTransId(clickTransId);
        clickReq.setServiceId(Integer.valueOf(clickConfig.getServiceId()));
        clickReq.setClickPaydocId(clickTransId + 10);
        clickReq.setMerchantTransId(String.valueOf(orderId));
        clickReq.setMerchantPrepareId(prepareId);
        clickReq.setAmount(amount);
        clickReq.setAction(1);
        clickReq.setError(clickError);
        clickReq.setErrorNote(clickError < 0 ? "User rejected payment on Click" : "Success");
        clickReq.setSignTime(signTime);
        clickReq.setSignString(signString);

        ClickResponse response = clickService.processComplete(clickReq);

        Map<String, Object> result = new HashMap<>();
        result.put("sentRequest", clickReq);
        result.put("rawSignFormula", "MD5(" + clickTransId + " + " + clickConfig.getServiceId() + " + SECRET_KEY + " + orderId + " + " + prepareId + " + " + amount + " + 1 + " + signTime + ")");
        result.put("generatedSign", signString);
        result.put("receivedResponse", response);

        return ResponseEntity.ok(result);
    }
}
