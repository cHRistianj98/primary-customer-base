package com.github.christianj98.primarycustomerbase.service;

import com.github.christianj98.primarycustomerbase.dto.OrderCreateDto;
import com.github.christianj98.primarycustomerbase.dto.OrderDto;
import com.github.christianj98.primarycustomerbase.dto.OrderUpdateDto;
import com.github.christianj98.primarycustomerbase.entity.Customer;
import com.github.christianj98.primarycustomerbase.entity.Order;
import com.github.christianj98.primarycustomerbase.mapper.OrderMapperService;
import com.github.christianj98.primarycustomerbase.repository.CustomerRepository;
import com.github.christianj98.primarycustomerbase.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final OrderMapperService orderMapperService;

    public List<OrderDto> findAll() {
        return orderMapperService.mapFrom(orderRepository.findAllWithCustomerAndAddress());
    }

    public OrderDto saveOrder(final OrderCreateDto orderCreateDto) {
        Customer customer = customerRepository.findById(orderCreateDto.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Customer not found with given id: %s", orderCreateDto.getCustomerId())));
        Order order = createOrder(orderCreateDto, customer);
        return orderMapperService.mapFrom(orderRepository.save(order));
    }

    private Order createOrder(final OrderCreateDto orderCreateDto, final Customer customer) {
        Order order = new Order();
        order.setDate(orderCreateDto.getDate());
        order.setAmount(orderCreateDto.getAmount());
        order.setCustomer(customer);
        return order;
    }

    public OrderDto findById(final int id) {
        return orderRepository.findById(id)
                .map(orderMapperService::mapFrom)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Order not found with given id: %s", id)));
    }

    public OrderDto update(final OrderUpdateDto orderUpdateDto, final int id) {
        return orderRepository.findById(id)
                .map(mapOrder(orderUpdateDto))
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Order not found with given id: %s", id)));
    }

    public void delete(final int id) {
        if (!orderRepository.existsById(id)) {
            throw new EntityNotFoundException(String.format("Order not found with given id: %s", id));
        }
        orderRepository.deleteById(id);
    }

    private Function<Order, OrderDto> mapOrder(final OrderUpdateDto orderUpdateDto) {
        return order -> {
            order.setAmount(orderUpdateDto.getAmount());
            order.setDate(orderUpdateDto.getDate());
            return orderMapperService.mapFrom(order);
        };
    }
}
