package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClickResponse {

    @JsonProperty("click_trans_id")
    private Long clickTransId;

    @JsonProperty("merchant_trans_id")
    private String merchantTransId;

    @JsonProperty("merchant_prepare_id")
    private Long merchantPrepareId;

    @JsonProperty("merchant_confirm_id")
    private Long merchantConfirmId;

    @JsonProperty("error")
    private Integer error;

    @JsonProperty("error_note")
    private String errorNote;

    public ClickResponse() {
    }

    public static ClickResponse successPrepare(Long clickTransId, String merchantTransId, Long merchantPrepareId) {
        ClickResponse res = new ClickResponse();
        res.clickTransId = clickTransId;
        res.merchantTransId = merchantTransId;
        res.merchantPrepareId = merchantPrepareId;
        res.error = 0;
        res.errorNote = "Success";
        return res;
    }

    public static ClickResponse successComplete(Long clickTransId, String merchantTransId, Long merchantConfirmId) {
        ClickResponse res = new ClickResponse();
        res.clickTransId = clickTransId;
        res.merchantTransId = merchantTransId;
        res.merchantConfirmId = merchantConfirmId;
        res.error = 0;
        res.errorNote = "Success";
        return res;
    }

    public static ClickResponse error(Long clickTransId, String merchantTransId, int errorCode, String errorNote) {
        ClickResponse res = new ClickResponse();
        res.clickTransId = clickTransId;
        res.merchantTransId = merchantTransId;
        res.error = errorCode;
        res.errorNote = errorNote;
        return res;
    }

    public static ClickResponse error(int errorCode, String errorNote) {
        ClickResponse res = new ClickResponse();
        res.error = errorCode;
        res.errorNote = errorNote;
        return res;
    }

    // Getters and Setters
    public Long getClickTransId() {
        return clickTransId;
    }

    public void setClickTransId(Long clickTransId) {
        this.clickTransId = clickTransId;
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

    public Long getMerchantConfirmId() {
        return merchantConfirmId;
    }

    public void setMerchantConfirmId(Long merchantConfirmId) {
        this.merchantConfirmId = merchantConfirmId;
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
}
