package com.github.christianj98.primarycustomerbase.service;

import com.github.christianj98.primarycustomerbase.dto.CustomerDto;
import com.github.christianj98.primarycustomerbase.entity.Customer;

import java.util.List;

public interface CustomerService {
    Customer save(CustomerDto customerDto);

    List<Customer> findAll();

    CustomerDto findById(int id);

    CustomerDto update(CustomerDto customerDto, int id);
}
