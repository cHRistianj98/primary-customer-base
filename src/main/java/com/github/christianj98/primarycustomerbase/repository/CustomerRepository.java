package com.github.christianj98.primarycustomerbase.repository;

import com.github.christianj98.primarycustomerbase.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

}
