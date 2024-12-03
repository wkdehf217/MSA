package com.sparta.msa_exam.client.order.orders;

import com.sparta.msa_exam.client.order.core.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
@Setter
public class OrderSearchDto {
    private OrderStatus status;
    private List<Long> productIds;
    private String sortBy;
    private Pageable pageable;
}