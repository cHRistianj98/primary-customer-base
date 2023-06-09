package com.github.christianj98.primarycustomerbase.repository;

import com.github.christianj98.primarycustomerbase.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query("SELECT o FROM Order o JOIN FETCH o.customer c JOIN FETCH c.address a")
    List<Order> findAllWithCustomerAndAddress();

    <S extends Order> S save(S entity);

    Optional<Order> findById(Integer integer);

    boolean existsById(Integer integer);

    void deleteById(Integer integer);
}
