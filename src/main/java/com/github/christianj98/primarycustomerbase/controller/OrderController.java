package com.github.christianj98.primarycustomerbase.controller;

import com.github.christianj98.primarycustomerbase.dto.OrderCreateDto;
import com.github.christianj98.primarycustomerbase.dto.OrderDto;
import com.github.christianj98.primarycustomerbase.dto.OrderUpdateDto;
import com.github.christianj98.primarycustomerbase.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping(path = "/orders")
@RequiredArgsConstructor
@Api(tags = "Order Controller")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    @ApiOperation("Find all orders")
    public ResponseEntity<List<OrderDto>> findAllOrders() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @PostMapping
    @ApiOperation("Create order")
    public ResponseEntity<OrderDto> createOrder(@RequestBody @Valid OrderCreateDto orderCreateDto) {
        OrderDto createdOrder = orderService.saveOrder(orderCreateDto);
        URI location = fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdOrder.getOrderId())
                .toUri();
        return ResponseEntity.created(location).body(createdOrder);
    }

    @GetMapping("/{id}")
    @ApiOperation("Find order with specific id")
    public ResponseEntity<OrderDto> findById(@PathVariable final int id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @PutMapping("/{id}")
    @ApiOperation("Update order with specific id")
    public ResponseEntity<OrderDto> updateOrder(@RequestBody @Valid OrderUpdateDto orderUpdateDto,
                                                @PathVariable int id) {
        return ResponseEntity.ok(orderService.update(orderUpdateDto, id));
    }

}
