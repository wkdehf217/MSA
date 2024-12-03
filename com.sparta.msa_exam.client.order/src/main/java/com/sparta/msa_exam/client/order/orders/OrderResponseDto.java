package com.sparta.msa_exam.client.order.orders;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponseDto implements Serializable {

    private Long orderId;
    private String status;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private List<Long> productIds;
    private String message;

    public OrderResponseDto(List<Long> productIds, String message) {
        this.productIds = productIds;
        this.message = message;
    }

    public OrderResponseDto(Long id, String name, LocalDateTime createdAt, String createdBy, LocalDateTime updatedAt, String updatedBy, List<Long> productIds) {
        this.orderId = id;
        this.status = name;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.productIds = productIds;
    }
}
