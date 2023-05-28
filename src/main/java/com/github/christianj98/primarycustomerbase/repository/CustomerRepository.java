package com.github.christianj98.primarycustomerbase.repository;

import com.github.christianj98.primarycustomerbase.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    @Override
    Optional<Customer> findById(Integer id);

    @Override
    <S extends Customer> S save(S customer);

    @Override
    boolean existsById(Integer id);

    boolean existsByFirstNameAndLastName(String firstName, String lastName);
}
