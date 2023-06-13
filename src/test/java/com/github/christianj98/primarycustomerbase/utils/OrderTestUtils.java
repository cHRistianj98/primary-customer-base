package com.github.christianj98.primarycustomerbase.utils;

import com.github.christianj98.primarycustomerbase.dto.OrderCreateDto;
import com.github.christianj98.primarycustomerbase.dto.OrderDto;
import com.github.christianj98.primarycustomerbase.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.FIRST_NAME;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.LAST_NAME;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.createCustomer;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.createCustomerDto;

public class OrderTestUtils {
    public static final LocalDateTime ORDER_DATE = LocalDateTime.of(2020, 2, 3, 1, 2, 4);
    public static final BigDecimal AMOUNT = new BigDecimal("999.99");
    public static final String ORDERS_URI = "/orders";
    public static final String ORDERS_URI_WITH_ID = "/orders/{id}";
    public static final int ID = 1;


    private OrderTestUtils() {
        // private
    }

    public static OrderDto createOrderDto(final LocalDateTime orderDate, final BigDecimal amount) {
        OrderDto orderDto = new OrderDto();
        orderDto.setDate(orderDate);
        orderDto.setAmount(amount);
        orderDto.setCustomerDto(createCustomerDto(FIRST_NAME, LAST_NAME));
        return orderDto;
    }

    public static OrderCreateDto createOrderCreateDto(final LocalDateTime orderDate,
                                                      final BigDecimal amount,
                                                      final int customerId) {
        OrderCreateDto orderDto = new OrderCreateDto();
        orderDto.setDate(orderDate);
        orderDto.setAmount(amount);
        orderDto.setCustomerId(customerId);
        return orderDto;
    }

    public static Order createOrder(final LocalDateTime orderDate, final BigDecimal amount) {
        Order order = new Order();
        order.setDate(orderDate);
        order.setAmount(amount);
        order.setCustomer(createCustomer(FIRST_NAME, LAST_NAME));
        return order;
    }

}
