package com.github.christianj98.primarycustomerbase.service;

import com.github.christianj98.primarycustomerbase.dto.OrderCreateDto;
import com.github.christianj98.primarycustomerbase.dto.OrderDto;
import com.github.christianj98.primarycustomerbase.dto.OrderUpdateDto;
import com.github.christianj98.primarycustomerbase.entity.Order;
import com.github.christianj98.primarycustomerbase.mapper.OrderMapperService;
import com.github.christianj98.primarycustomerbase.repository.CustomerRepository;
import com.github.christianj98.primarycustomerbase.repository.OrderRepository;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.AMOUNT;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.ID;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.ORDER_DATE;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.createOrder;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.createOrderCreateDto;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.createOrderDto;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.createOrderUpdateDto;
import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link OrderServiceImpl}
 */
@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {
    @InjectMocks
    private OrderServiceImpl orderService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapperService orderMapperService;
    @Mock
    private CustomerRepository customerRepository;
    @Captor
    private ArgumentCaptor<Order> orderCaptor;
    private Order order;
    private OrderDto orderDto;
    private OrderCreateDto orderCreateDto;
    private OrderUpdateDto orderUpdateDto;

    @BeforeEach
    public void init() {
        order = createOrder(ORDER_DATE, AMOUNT);
        orderDto = createOrderDto(ORDER_DATE, AMOUNT);
        orderCreateDto = createOrderCreateDto(ORDER_DATE, AMOUNT, order.getCustomer().getId());
        orderUpdateDto = createOrderUpdateDto();
    }

    @Test
    @DisplayName("Find all orders with customer and address information")
    public void findAll_AllOrdersWithCustomerAndAddressAreFound() {
        // given
        when(orderRepository.findAllWithCustomerAndAddress()).thenReturn(List.of(order));
        when(orderMapperService.mapFrom(anyList())).thenReturn(List.of(orderDto));

        // when
        final List<OrderDto> orders = orderService.findAll();

        // then
        assertThat(orders).isNotEmpty();
        assertThat(orders).extracting(OrderDto::getDate, OrderDto::getAmount)
                .isEqualTo(List.of(Tuple.tuple(orderDto.getDate(), orderDto.getAmount())));
        verify(orderRepository).findAllWithCustomerAndAddress();
        verify(orderMapperService).mapFrom(eq(List.of(order)));
    }

    @Test
    @DisplayName("Create order for existing customer")
    public void save_createOrderForExistingCustomer() {
        // given
        when(customerRepository.findById(anyInt())).thenReturn(ofNullable(order.getCustomer()));
        when(orderRepository.save(any())).thenReturn(order);

        // when
        orderService.saveOrder(orderCreateDto);

        // then
        verify(orderRepository).save(order);
        verify(orderMapperService).mapFrom(order);
    }

    @Test
    @DisplayName("Create order but customer was not found")
    public void save_createOrderButCustomerDoesNotExist() {
        // given
        when(customerRepository.findById(anyInt())).thenReturn(Optional.empty());

        // when + then
        assertThatThrownBy(() -> orderService.saveOrder(orderCreateDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Customer not found");
    }

    @Test
    @DisplayName("Find order by id")
    public void findById_orderFound() {
        // given
        when(orderRepository.findById(ID)).thenReturn(Optional.of(order));
        when(orderMapperService.mapFrom(any(Order.class))).thenReturn(orderDto);

        // when
        final OrderDto foundOrder = orderService.findById(ID);

        // then
        assertThat(foundOrder.getOrderId()).isEqualTo(orderDto.getOrderId());
        assertThat(foundOrder.getDate()).isEqualTo(orderDto.getDate());
        assertThat(foundOrder.getAmount()).isEqualTo(orderDto.getAmount());
        assertThat(foundOrder.getCustomerDto()).isEqualTo(orderDto.getCustomerDto());
        verify(orderRepository).findById(eq(ID));
        verify(orderMapperService).mapFrom(eq(order));
    }

    @Test
    @DisplayName("Find order by id but order does not exist")
    public void findById_orderNotFound() {
        // given
        when(orderRepository.findById(anyInt())).thenReturn(Optional.empty());

        // when + then
        assertThatThrownBy(() -> orderService.findById(ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Order not found")
                .hasMessageContaining(String.valueOf(ID));
    }

    @Test
    public void updateOrder_orderUpdatedSuccessfully() {
        // given
        when(orderRepository.findById(ID)).thenReturn(Optional.of(order));
        when(orderMapperService.mapFrom(any(Order.class))).thenReturn(orderDto);

        // when
        orderService.update(orderUpdateDto, ID);

        // then
        verify(orderMapperService).mapFrom(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getDate()).isEqualTo(orderUpdateDto.getDate());
        assertThat(orderCaptor.getValue().getAmount()).isEqualTo(orderUpdateDto.getAmount());
    }

    @Test
    public void updateOrder_orderNotFound() {
        // given
        when(orderRepository.findById(ID)).thenReturn(Optional.empty());

        // when + then
        assertThatThrownBy(() -> orderService.update(orderUpdateDto, ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.valueOf(ID));
        verifyNoInteractions(orderMapperService);
    }

}
