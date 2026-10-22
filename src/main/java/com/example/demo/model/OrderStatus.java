package com.example.demo.model;

public enum OrderStatus {
    NEW,        // Yangi yaratilgan buyurtma
    PREPARED,   // Click tomonidan tekshirilgan va tayyorlangan
    PAID,       // To'lov muvaffaqiyatli yakunlangan
    CANCELLED   // Bekor qilingan yoki rad etilgan
}
