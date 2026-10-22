package com.example.demo.dto;

public class CardVerifyResponse {
    private boolean valid;
    private String cardType; // UZCARD, HUMO, VISA, MASTERCARD, UNKNOWN
    private String maskedPan;
    private String error;
    private boolean smsSent;
    private String simulatedOtpCode; // Sinov uchun SMS xabar kodi

    public CardVerifyResponse() {}

    public static CardVerifyResponse success(String cardType, String maskedPan, String simulatedOtpCode) {
        CardVerifyResponse res = new CardVerifyResponse();
        res.valid = true;
        res.cardType = cardType;
        res.maskedPan = maskedPan;
        res.smsSent = true;
        res.simulatedOtpCode = simulatedOtpCode;
        return res;
    }

    public static CardVerifyResponse fail(String error) {
        CardVerifyResponse res = new CardVerifyResponse();
        res.valid = false;
        res.error = error;
        return res;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getMaskedPan() {
        return maskedPan;
    }

    public void setMaskedPan(String maskedPan) {
        this.maskedPan = maskedPan;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isSmsSent() {
        return smsSent;
    }

    public void setSmsSent(boolean smsSent) {
        this.smsSent = smsSent;
    }

    public String getSimulatedOtpCode() {
        return simulatedOtpCode;
    }

    public void setSimulatedOtpCode(String simulatedOtpCode) {
        this.simulatedOtpCode = simulatedOtpCode;
    }
}
