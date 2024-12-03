package com.sparta.msa_exam.client.order.orders;

import com.sparta.msa_exam.client.order.core.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {
}
