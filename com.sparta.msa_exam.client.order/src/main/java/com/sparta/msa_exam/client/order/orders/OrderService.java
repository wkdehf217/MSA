package com.sparta.msa_exam.client.order.orders;

import com.sparta.msa_exam.client.order.RestPage;
import com.sparta.msa_exam.client.order.core.client.ProductClient;
import com.sparta.msa_exam.client.order.core.client.ProductResponseDto;
import com.sparta.msa_exam.client.order.core.domain.Order;
import com.sparta.msa_exam.client.order.core.enums.OrderStatus;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.config.TaskExecutionOutcome;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    @Transactional
    @CircuitBreaker(name = "orderService", fallbackMethod = "fallbackGetProductDetails")
    //@CachePut(cacheNames = "orderPutCache", key = "args[0]")
    public OrderResponseDto createOrder(OrderRequestDto requestDto, String userId) {
        for (Long productId : requestDto.getProductIds()) {
            ProductResponseDto product = productClient.getProduct(productId);
            log.info("############################ Product 수량 확인 : " + product.getQuantity());
            if (product.getQuantity() < 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product with ID " + productId + " is out of stock.");
            }
        }

        for (Long productId : requestDto.getProductIds()) {
            productClient.reduceProductQuantity(productId, 1);
        }


        Order order = Order.createOrder(requestDto.getProductIds(), userId);
        Order savedOrder = orderRepository.save(order);
        return toResponseDto(savedOrder);
    }

    public OrderResponseDto fallbackGetProductDetails(OrderRequestDto orderRequestDto, String Param,Throwable t) {

        return new OrderResponseDto(orderRequestDto.getProductIds(), "잠시 후에 주문 추가를 요청 해주세요.");
    }

    //@Cacheable(cacheNames = "orderAllCache", key = "#role")
    @Transactional(readOnly = true)
    public RestPage<OrderResponseDto> getOrders(OrderSearchDto searchDto, Pageable pageable, String role, String userId) {
        return orderRepository.searchOrders(searchDto, pageable,role, userId);
    }

    //@Cacheable(cacheNames = "orderCache", key = "args[0]")
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .filter(o -> o.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found or has been deleted"));
        return toResponseDto(order);
    }

    //@CacheEvict(cacheNames = "orderCache",allEntries = true)
    @Transactional
    public OrderResponseDto updateOrder(Long orderId, OrderRequestDto requestDto,String userId) {
        Order order = orderRepository.findById(orderId)
                .filter(o -> o.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found or has been deleted"));

        order.updateOrder(requestDto.getProductIds(), userId, OrderStatus.valueOf(requestDto.getStatus()));
        Order updatedOrder = orderRepository.save(order);

        return toResponseDto(updatedOrder);
    }

    @Transactional
    public void deleteOrder(Long orderId, String deletedBy) {
        Order order = orderRepository.findById(orderId)
                .filter(o -> o.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found or has been deleted"));
        order.deleteOrder(deletedBy);
        orderRepository.save(order);
    }

    private OrderResponseDto toResponseDto(Order order) {
        return new OrderResponseDto(
                order.getId(),
                order.getStatus().name(),
                order.getCreatedAt(),
                order.getCreatedBy(),
                order.getUpdatedAt(),
                order.getUpdatedBy(),
                order.getProductIds()
        );
    }
}