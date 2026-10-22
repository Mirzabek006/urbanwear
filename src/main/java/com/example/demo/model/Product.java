package com.example.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String category;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    private BigDecimal oldPrice;

    @Column(length = 1000)
    private String description;

    private String imageUrl;

    private Integer stock = 100;
    private Double rating = 4.8;
    private Integer reviewsCount = 42;
    private Integer discountPercent = 0;
    private String sizes; // "S, M, L, XL" yoki "39, 40, 41, 42"
    private String colors; // "Qora, Ko'k, Kulrang"
    private Boolean isFeatured = false;

    public Product() {}

    public Product(String name, String category, BigDecimal price, BigDecimal oldPrice,
                   String description, String imageUrl, Double rating, Integer reviewsCount,
                   Integer discountPercent, String sizes, String colors, Boolean isFeatured) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.oldPrice = oldPrice;
        this.description = description;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.reviewsCount = reviewsCount;
        this.discountPercent = discountPercent;
        this.sizes = sizes;
        this.colors = colors;
        this.isFeatured = isFeatured;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(BigDecimal oldPrice) {
        this.oldPrice = oldPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getReviewsCount() {
        return reviewsCount;
    }

    public void setReviewsCount(Integer reviewsCount) {
        this.reviewsCount = reviewsCount;
    }

    public Integer getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Integer discountPercent) {
        this.discountPercent = discountPercent;
    }

    public String getSizes() {
        return sizes;
    }

    public void setSizes(String sizes) {
        this.sizes = sizes;
    }

    public String getColors() {
        return colors;
    }

    public void setColors(String colors) {
        this.colors = colors;
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }
}
