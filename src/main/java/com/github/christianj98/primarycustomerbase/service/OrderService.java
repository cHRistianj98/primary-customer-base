package com.github.christianj98.primarycustomerbase.service;

import com.github.christianj98.primarycustomerbase.dto.OrderCreateDto;
import com.github.christianj98.primarycustomerbase.dto.OrderDto;
import com.github.christianj98.primarycustomerbase.dto.OrderUpdateDto;

import java.util.List;

public interface OrderService {
    List<OrderDto> findAll();

    OrderDto saveOrder(OrderCreateDto orderCreateDto);

    OrderDto findById(int id);

    OrderDto update(OrderUpdateDto orderUpdateDto, int id);
}
