package com.example.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "click_transactions")
public class ClickTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long clickTransId;
    private Long clickPaydocId;
    private Long orderId;
    private BigDecimal amount;
    private Integer action; // 0 = prepare, 1 = complete
    private Integer status; // Click error response kodi: 0=Success, <0 = Xato
    private String signTime;
    private String signString;
    private String note;
    private LocalDateTime createdAt;

    public ClickTransaction() {
    }

    public ClickTransaction(Long clickTransId, Long clickPaydocId, Long orderId, BigDecimal amount,
                            Integer action, Integer status, String signTime, String signString, String note) {
        this.clickTransId = clickTransId;
        this.clickPaydocId = clickPaydocId;
        this.orderId = orderId;
        this.amount = amount;
        this.action = action;
        this.status = status;
        this.signTime = signTime;
        this.signString = signString;
        this.note = note;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getClickTransId() {
        return clickTransId;
    }

    public void setClickTransId(Long clickTransId) {
        this.clickTransId = clickTransId;
    }

    public Long getClickPaydocId() {
        return clickPaydocId;
    }

    public void setClickPaydocId(Long clickPaydocId) {
        this.clickPaydocId = clickPaydocId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
