package com.example.demo.dto;

import java.util.Map;

public class ClickRequest {
    private Long clickTransId;
    private Integer serviceId;
    private Long clickPaydocId;
    private String merchantTransId;
    private Long merchantPrepareId;
    private String amount;
    private Integer action;
    private Integer error;
    private String errorNote;
    private String signTime;
    private String signString;

    public static ClickRequest fromMap(Map<String, String> params) {
        ClickRequest req = new ClickRequest();
        req.clickTransId = parseLong(params.get("click_trans_id"));
        req.serviceId = parseInt(params.get("service_id"));
        req.clickPaydocId = parseLong(params.get("click_paydoc_id"));
        req.merchantTransId = params.get("merchant_trans_id");
        req.merchantPrepareId = parseLong(params.get("merchant_prepare_id"));
        req.amount = params.get("amount");
        req.action = parseInt(params.get("action"));
        req.error = parseInt(params.get("error"));
        req.errorNote = params.get("error_note");
        req.signTime = params.get("sign_time");
        req.signString = params.get("sign_string");
        return req;
    }

    private static Long parseLong(String val) {
        if (val == null || val.trim().isEmpty()) return null;
        try {
            return Long.parseLong(val.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Integer parseInt(String val) {
        if (val == null || val.trim().isEmpty()) return null;
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // Getters and Setters
    public Long getClickTransId() {
        return clickTransId;
    }

    public void setClickTransId(Long clickTransId) {
        this.clickTransId = clickTransId;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Long getClickPaydocId() {
        return clickPaydocId;
    }

    public void setClickPaydocId(Long clickPaydocId) {
        this.clickPaydocId = clickPaydocId;
    }

    public String getMerchantTransId() {
        return merchantTransId;
    }

    public void setMerchantTransId(String merchantTransId) {
        this.merchantTransId = merchantTransId;
    }

    public Long getMerchantPrepareId() {
        return merchantPrepareId;
    }

    public void setMerchantPrepareId(Long merchantPrepareId) {
        this.merchantPrepareId = merchantPrepareId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public String getErrorNote() {
        return errorNote;
    }

    public void setErrorNote(String errorNote) {
        this.errorNote = errorNote;
    }

    public String getSignTime() {
        return signTime;
    }

    public void setSignTime(String signTime) {
        this.signTime = signTime;
    }

    public String getSignString() {
        return signString;
    }

    public void setSignString(String signString) {
        this.signString = signString;
    }
}
