package com.github.christianj98.primarycustomerbase.controller;

import com.github.christianj98.primarycustomerbase.dto.OrderDto;
import com.github.christianj98.primarycustomerbase.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/orders")
@RequiredArgsConstructor
@Api(tags = "Order Controller")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    @ApiOperation("Find all customers")
    public ResponseEntity<List<OrderDto>> findAllCustomers() {
        return ResponseEntity.ok(orderService.findAll());
    }
}
