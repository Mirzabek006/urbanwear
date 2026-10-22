package com.example.demo.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ClickUtil {

    private ClickUtil() {}

    /**
     * MD5 hash hisoblash (kichik harflar bilan hex string).
     */
    public static String md5(String input) {
        if (input == null) {
            return "";
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algoritmi topilmadi", e);
        }
    }

    /**
     * Prepare bosqichi uchun MD5 imzo yaratish:
     * MD5(click_trans_id + service_id + SECRET_KEY + merchant_trans_id + amount + action + sign_time)
     */
    public static String generatePrepareSign(Long clickTransId, Object serviceId, String secretKey,
                                             String merchantTransId, String amount, Integer action, String signTime) {
        String raw = String.valueOf(clickTransId)
                + serviceId
                + secretKey
                + merchantTransId
                + amount
                + action
                + signTime;
        return md5(raw);
    }

    /**
     * Complete bosqichi uchun MD5 imzo yaratish:
     * MD5(click_trans_id + service_id + SECRET_KEY + merchant_trans_id + merchant_prepare_id + amount + action + sign_time)
     */
    public static String generateCompleteSign(Long clickTransId, Object serviceId, String secretKey,
                                              String merchantTransId, Long merchantPrepareId,
                                              String amount, Integer action, String signTime) {
        String raw = String.valueOf(clickTransId)
                + serviceId
                + secretKey
                + merchantTransId
                + merchantPrepareId
                + amount
                + action
                + signTime;
        return md5(raw);
    }

    /**
     * Foydalanuvchini Click to'lov sahifasiga yo'naltirish URL'ini hosil qilish
     */
    public static String buildPaymentUrl(String checkoutUrl, String serviceId, String merchantId,
                                         String merchantTransId, String amount, String returnUrl) {
        try {
            String encodedReturn = URLEncoder.encode(returnUrl, StandardCharsets.UTF_8.name());
            return checkoutUrl
                    + "?service_id=" + serviceId
                    + "&merchant_id=" + merchantId
                    + "&amount=" + amount
                    + "&transaction_param=" + merchantTransId
                    + "&return_url=" + encodedReturn;
        } catch (UnsupportedEncodingException e) {
            return checkoutUrl
                    + "?service_id=" + serviceId
                    + "&merchant_id=" + merchantId
                    + "&amount=" + amount
                    + "&transaction_param=" + merchantTransId;
        }
    }
}
