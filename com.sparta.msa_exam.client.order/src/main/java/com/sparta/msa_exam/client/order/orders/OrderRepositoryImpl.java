package com.sparta.msa_exam.client.order.orders;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.msa_exam.client.order.RestPage;
import com.sparta.msa_exam.client.order.core.domain.Order;
import com.sparta.msa_exam.client.order.core.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.sparta.msa_exam.client.order.core.domain.QOrder.order;


@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public RestPage<OrderResponseDto> searchOrders(OrderSearchDto searchDto, Pageable pageable, String role, String userId) {

        List<OrderSpecifier<?>> orders = getAllOrderSpecifiers(pageable);

        QueryResults<Order> results = queryFactory
                .selectFrom(order)
                .where(
                        statusEq(searchDto.getStatus()),
                        productIdsIn(searchDto.getProductIds()),
                        userCheck(role, userId)
                )
                .orderBy(orders.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<OrderResponseDto> content = results.getResults().stream()
                .map(Order::toResponseDto)
                .collect(Collectors.toList());
        long total = results.getTotal();

        PageImpl<OrderResponseDto> page = new PageImpl<>(content, pageable, total);
        return new RestPage<>(page);
    }

    private BooleanExpression statusEq(OrderStatus status) {
        return status != null ? order.status.eq(status) : null;
    }
    private BooleanExpression userCheck(String role, String userId) {
        return role.equals("MEMBER")? order.createdBy.eq(userId): null;
    }

    private BooleanExpression productIdsIn(List<Long> productIds) {
        return productIds != null && !productIds.isEmpty() ? order.productIds.any().in(productIds) : null;
    }

    private List<OrderSpecifier<?>> getAllOrderSpecifiers(Pageable pageable) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (pageable.getSort() != null) {
            for (Sort.Order sortOrder : pageable.getSort()) {
                com.querydsl.core.types.Order direction = sortOrder.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC;
                switch (sortOrder.getProperty()) {
                    case "createdAt":
                        orders.add(new OrderSpecifier<>(direction, order.createdAt));
                        break;
                    case "status":
                        orders.add(new OrderSpecifier<>(direction, order.status));
                        break;
                    default:
                        break;
                }
            }
        }

        return orders;
    }
}