package com.github.christianj98.primarycustomerbase.service;

import com.github.christianj98.primarycustomerbase.dto.AddressDto;

import java.util.List;

public interface AddressService {
    List<AddressDto> findAll();

    AddressDto createAddress(AddressDto addressDto);

    AddressDto findById(int id);

    AddressDto update(AddressDto addressDto, int id);

    void delete(int id);
}
