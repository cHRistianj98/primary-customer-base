package com.github.christianj98.primarycustomerbase.controller;

import com.github.christianj98.primarycustomerbase.dto.AddressDto;
import com.github.christianj98.primarycustomerbase.service.AddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("addresses")
@RequiredArgsConstructor
@Api(tags = "Address Controller")
public class AddressController {
    private final AddressService addressService;

    @GetMapping
    @ApiOperation("Find all addresses")
    public ResponseEntity<List<AddressDto>> findAllAddresses() {
        return ResponseEntity.ok(addressService.findAll());
    }

    @PostMapping
    @ApiOperation("Create address")
    public ResponseEntity<AddressDto> createAddress(@RequestBody @Valid AddressDto addressDto) {
        final AddressDto createdAddress = addressService.createAddress(addressDto);
        final URI location = fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdAddress.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdAddress);
    }

    @GetMapping("/{id}")
    @ApiOperation("Find address by id")
    public ResponseEntity<AddressDto> findAddressById(@PathVariable int id) {
        return ResponseEntity.ok().body(addressService.findById(id));
    }

    @PutMapping("/{id}")
    @ApiOperation("Update address")
    public ResponseEntity<AddressDto> updateAddress(@RequestBody @Valid AddressDto addressDto, @PathVariable int id) {
        return ResponseEntity.ok(addressService.update(addressDto, id));
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Delete address")
    public ResponseEntity<Void> deleteAddress(@PathVariable final int id) {
        addressService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
