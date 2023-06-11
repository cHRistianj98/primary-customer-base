package com.github.christianj98.primarycustomerbase.mapper;

import com.github.christianj98.primarycustomerbase.dto.OrderDto;
import com.github.christianj98.primarycustomerbase.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderMapperService {
    private final CustomerMapperService customerMapperService;

    public List<OrderDto> mapFrom(List<Order> orders) {
        return orders.stream()
                .map(this::mapFrom)
                .collect(Collectors.toList());
    }

    public OrderDto mapFrom(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setDate(order.getDate());
        orderDto.setAmount(order.getAmount());
        orderDto.setCustomerDto(customerMapperService.mapFrom(order.getCustomer()));
        return orderDto;
    }
}
