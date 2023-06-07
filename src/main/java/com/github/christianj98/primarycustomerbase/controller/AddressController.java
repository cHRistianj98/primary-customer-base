package com.github.christianj98.primarycustomerbase.controller;

import com.github.christianj98.primarycustomerbase.dto.AddressDto;
import com.github.christianj98.primarycustomerbase.service.AddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("addresses")
@RequiredArgsConstructor
@Api(tags = "Address Controller")
public class AddressController {
    private final AddressService addressService;

    @GetMapping
    @ApiOperation("Find all addresses")
    public ResponseEntity<List<AddressDto>> findAllCustomers() {
        return ResponseEntity.ok(addressService.findAll());
    }
}
