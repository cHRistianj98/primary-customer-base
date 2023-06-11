package com.github.christianj98.primarycustomerbase.repository;

import com.github.christianj98.primarycustomerbase.entity.Customer;
import com.github.christianj98.primarycustomerbase.entity.Order;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.FIRST_NAME;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.LAST_NAME;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.createCustomer;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.AMOUNT;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.ORDER_DATE;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.createOrder;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    private Order order;

    private Customer customer;

    @BeforeEach
    public void init() {
        customer = customerRepository.save(createCustomer(FIRST_NAME, LAST_NAME));
        order = orderRepository.save(createOrder(ORDER_DATE, AMOUNT));
    }

    @Test
    public void whenFindAllWithCustomerAndAddress_ThenReturnAllUsers() {
        // when
        final List<Order> orders = orderRepository.findAllWithCustomerAndAddress();

        // then
        assertThat(orders).isNotEmpty();
        assertThat(orders).extracting(Order::getDate, Order::getAmount)
                .isEqualTo(List.of(Tuple.tuple(order.getDate(), order.getAmount())));
    }

    @Test
    public void whenSaveOrder_theReturnCreatedOrder() {
        // when
        final Order savedOrder = orderRepository.save(order);

        // then
        assertThat(savedOrder.getId()).isEqualTo(order.getId());
        assertThat(savedOrder.getDate()).isEqualTo(order.getDate());
        assertThat(savedOrder.getAmount()).isEqualTo(order.getAmount());
        assertThat(savedOrder.getCustomer()).isEqualTo(order.getCustomer());
    }
}
