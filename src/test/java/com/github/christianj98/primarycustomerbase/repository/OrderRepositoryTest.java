package com.github.christianj98.primarycustomerbase.repository;

import com.github.christianj98.primarycustomerbase.entity.Customer;
import com.github.christianj98.primarycustomerbase.entity.Order;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;

import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.FIRST_NAME;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.LAST_NAME;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.createCustomer;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.AMOUNT;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.ORDER_DATE;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.createOrder;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
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

    @Test
    public void whenFindById_thenReturnOrder() {
        // when
        final Optional<Order> foundOrder = orderRepository.findById(order.getId());

        // then
        assertThat(foundOrder).isPresent();
        assertThat(foundOrder.get().getId()).isEqualTo(order.getId());
        assertThat(foundOrder.get().getDate()).isEqualTo(order.getDate());
        assertThat(foundOrder.get().getAmount()).isEqualTo(order.getAmount());
        assertThat(foundOrder.get().getCustomer()).isEqualTo(order.getCustomer());
    }

    @Test
    public void whenExistsById_thenReturnTrue() {
        // when
        final boolean orderExist = orderRepository.existsById(order.getId());

        // then
        assertThat(orderExist).isTrue();
    }

    @Test
    public void whenDeleteById_orderIsDeleted() {
        // when
        orderRepository.deleteById(order.getId());

        // then
        assertThat(orderRepository.existsById(order.getId())).isFalse();
    }
}
