package com.example.demo.dto;

public final class ClickErrorCode {
    public static final int SUCCESS = 0;
    public static final int SIGN_CHECK_FAILED = -1;
    public static final int INCORRECT_AMOUNT = -2;
    public static final int ACTION_NOT_FOUND = -3;
    public static final int ALREADY_PAID = -4;
    public static final int ORDER_NOT_FOUND = -5;
    public static final int TRANSACTION_NOT_FOUND = -6;
    public static final int FAILED_TO_UPDATE = -7;
    public static final int ERROR_IN_REQUEST = -8;
    public static final int TRANSACTION_CANCELLED = -9;

    private ClickErrorCode() {}
}
