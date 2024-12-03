package com.sparta.msa_exam.client.order.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sparta.msa_exam.client.order.core.enums.OrderStatus;
import com.sparta.msa_exam.client.order.orders.OrderResponseDto;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "orders")
public class Order implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ElementCollection
    @CollectionTable(name = "order_items", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "order_item_id")
    @JsonIgnore
    private List<Long> productIds;

    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private LocalDateTime deletedAt;
    private String deletedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = OrderStatus.CREATED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void updateStatus(OrderStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public static Order createOrder(List<Long> productIds, String createdBy) {
        return Order.builder()
                .productIds(productIds)
                .createdBy(createdBy)
                .status(OrderStatus.CREATED)
                .build();
    }

    public void updateOrder(List<Long> productIds, String updatedBy, OrderStatus status) {
        this.productIds = productIds;
        this.updatedBy = updatedBy;
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public void deleteOrder(String deletedBy) {
        this.deletedBy = deletedBy;
        this.deletedAt = LocalDateTime.now();
    }

    public OrderResponseDto toResponseDto() {
        return new OrderResponseDto(
                this.id,
                this.status.name(),
                this.createdAt,
                this.createdBy,
                this.updatedAt,
                this.updatedBy,
                this.productIds
        );
    }
}

