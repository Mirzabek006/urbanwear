package com.example.demo.model;

public enum PaymentMethod {
    CLICK("Click"),
    PAYME("Payme"),
    ALIF("Alif Nasiya"),
    CARD("Bank kartasi"),
    CASH("Naqd pul");

    private final String title;

    PaymentMethod(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
