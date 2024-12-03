package com.sparta.msa_exam.client.order.orders;

import com.sparta.msa_exam.client.order.RestPage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;


    @PostMapping
    public OrderResponseDto createOrder(@RequestBody OrderRequestDto orderRequestDto,
                                        @RequestHeader(value = "X-User-Id", required = true) String userId,
                                        @RequestHeader(value = "X-Role", required = true) String role) {

        return orderService.createOrder(orderRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<RestPage<OrderResponseDto>>getOrders(OrderSearchDto searchDto, Pageable pageable,
                                                                       @RequestHeader(value = "X-User-Id", required = true) String userId,
                                                                       @RequestHeader(value = "X-Role", required = true) String role) {

        if (!"MANAGER".equals(role)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access denied. User role is not MANAGER.");
        }
        RestPage<OrderResponseDto> result = orderService.getOrders(searchDto, pageable,role, userId);
        return ResponseEntity.ok()
                .body(result);
//        return orderService.getOrders(searchDto, pageable,role, userId);
    }

    @GetMapping("/{orderId}")
    public OrderResponseDto getOrderById(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId);
    }

    @PutMapping("/{orderId}")
    public OrderResponseDto updateOrder(@PathVariable Long orderId,
                                        @RequestBody OrderRequestDto orderRequestDto,
                                        @RequestHeader(value = "X-User-Id", required = true) String userId,
                                        @RequestHeader(value = "X-Role", required = true) String role) {
        return orderService.updateOrder(orderId, orderRequestDto, userId);
    }

    @DeleteMapping("/{orderId}")
    public void deleteOrder(@PathVariable Long orderId, @RequestParam String deletedBy) {
        orderService.deleteOrder(orderId, deletedBy);
    }
}
