package com.sparta.msa_exam.client.product.core;


import com.sparta.msa_exam.client.product.products.ProductRequestDto;
import com.sparta.msa_exam.client.product.products.ProductResponseDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Integer supplyPrice;
    private Integer quantity;

    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private LocalDateTime deletedAt;
    private String deletedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static Product createProduct(ProductRequestDto requestDto, String userId) {
        return Product.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .supplyPrice(requestDto.getSupplyPrice())
                .quantity(requestDto.getQuantity())
                .createdBy(userId)
                .build();
    }

    public void updateProduct(String name, String description, Integer supplyPrice, Integer quantity, String updatedBy) {
        this.name = name;
        this.description = description;
        this.supplyPrice = supplyPrice;
        this.quantity = quantity;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    public void deleteProduct(String deletedBy) {
        this.deletedBy = deletedBy;
        this.deletedAt = LocalDateTime.now();
    }

    public ProductResponseDto toResponseDto() {
        return new ProductResponseDto(
                this.id,
                this.name,
                this.description,
                this.supplyPrice,
                this.quantity,
                this.createdAt,
                this.createdBy,
                this.updatedAt,
                this.updatedBy
        );
    }

    public void reduceQuantity(int i) {
        this.quantity = this.quantity - i;
    }
}