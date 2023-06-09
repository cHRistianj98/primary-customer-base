package com.github.christianj98.primarycustomerbase.repository;

import com.github.christianj98.primarycustomerbase.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
    List<Address> findAll();

    boolean existsByStreetAndCity(String street, String city);

    <S extends Address> S save(S address);

    Optional<Address> findById(Integer id);
}
