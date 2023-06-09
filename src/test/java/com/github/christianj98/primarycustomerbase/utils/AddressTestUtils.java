package com.github.christianj98.primarycustomerbase.utils;

import com.github.christianj98.primarycustomerbase.dto.AddressDto;
import com.github.christianj98.primarycustomerbase.entity.Address;

public class AddressTestUtils {
    public static final String STREET = "Poniatowskiego";
    public static final String CITY = "Wroclaw";
    public static final String ADDRESSES_URI = "/addresses";
    public static final String ADDRESSES_WITH_ID_URI = ADDRESSES_URI + "/{id}";
    public static final int RANDOM_INT = 1;

    private AddressTestUtils() {
        // private
    }

    public static AddressDto createAddressDto(final String street, final String city) {
        AddressDto addressDto = new AddressDto();
        addressDto.setId(RANDOM_INT);
        addressDto.setStreet(street);
        addressDto.setCity(city);
        return addressDto;
    }

    public static Address createAddress(final String street, final String city) {
        Address address = new Address();
        address.setId(RANDOM_INT);
        address.setStreet(street);
        address.setCity(city);
        return address;
    }
}
