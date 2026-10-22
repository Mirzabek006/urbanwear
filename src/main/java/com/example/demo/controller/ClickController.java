package com.example.demo.controller;

import com.example.demo.dto.ClickRequest;
import com.example.demo.dto.ClickResponse;
import com.example.demo.service.ClickService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping({"/click", "/api/click"})
public class ClickController {

    private final ClickService clickService;

    public ClickController(ClickService clickService) {
        this.clickService = clickService;
    }

    /**
     * Click Prepare endpoint (action = 0)
     * Form-urlencoded formatida Click serveridan keladi.
     */
    @PostMapping(value = "/prepare", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ClickResponse prepareForm(@RequestParam Map<String, String> params) {
        ClickRequest req = ClickRequest.fromMap(params);
        if (req.getAction() == null) {
            req.setAction(0);
        }
        return clickService.processPrepare(req);
    }

    /**
     * JSON formatida ham qabul qilish (test vositalari yoki API testerlar uchun)
     */
    @PostMapping(value = "/prepare", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClickResponse prepareJson(@RequestBody ClickRequest req) {
        if (req.getAction() == null) {
            req.setAction(0);
        }
        return clickService.processPrepare(req);
    }

    /**
     * Click Complete endpoint (action = 1)
     * Form-urlencoded formatida Click serveridan keladi.
     */
    @PostMapping(value = "/complete", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ClickResponse completeForm(@RequestParam Map<String, String> params) {
        ClickRequest req = ClickRequest.fromMap(params);
        if (req.getAction() == null) {
            req.setAction(1);
        }
        return clickService.processComplete(req);
    }

    /**
     * JSON formatida ham qabul qilish
     */
    @PostMapping(value = "/complete", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClickResponse completeJson(@RequestBody ClickRequest req) {
        if (req.getAction() == null) {
            req.setAction(1);
        }
        return clickService.processComplete(req);
    }

    /**
     * Yagona endpoint (Click kabinetida bitta URL sozlangan bo'lsa)
     */
    @PostMapping(value = {"", "/process"}, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ClickResponse processForm(@RequestParam Map<String, String> params) {
        ClickRequest req = ClickRequest.fromMap(params);
        return clickService.process(req);
    }

    @PostMapping(value = {"", "/process"}, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClickResponse processJson(@RequestBody ClickRequest req) {
        return clickService.process(req);
    }
}
