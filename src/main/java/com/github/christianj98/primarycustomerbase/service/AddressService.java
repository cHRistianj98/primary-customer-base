package com.github.christianj98.primarycustomerbase.service;

import com.github.christianj98.primarycustomerbase.dto.AddressDto;

import java.util.List;

public interface AddressService {
    List<AddressDto> findAll();

    AddressDto createAddress(AddressDto addressDto);
}
