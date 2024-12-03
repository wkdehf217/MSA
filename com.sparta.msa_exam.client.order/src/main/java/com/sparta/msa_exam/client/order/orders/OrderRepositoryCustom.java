package com.sparta.msa_exam.client.order.orders;

import com.sparta.msa_exam.client.order.RestPage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepositoryCustom {
    RestPage<OrderResponseDto> searchOrders(OrderSearchDto searchDto, Pageable pageable, String role, String userId);
}
