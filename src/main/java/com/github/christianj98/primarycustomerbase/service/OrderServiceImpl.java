package com.github.christianj98.primarycustomerbase.service;

import com.github.christianj98.primarycustomerbase.dto.OrderDto;
import com.github.christianj98.primarycustomerbase.mapper.OrderMapperService;
import com.github.christianj98.primarycustomerbase.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapperService orderMapperService;
    public List<OrderDto> findAll() {
        return orderMapperService.mapFrom(orderRepository.findAllWithCustomerAndAddress());
    }
}
