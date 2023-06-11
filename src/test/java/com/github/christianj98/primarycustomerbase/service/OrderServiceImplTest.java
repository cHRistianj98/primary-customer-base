package com.github.christianj98.primarycustomerbase.service;

import com.github.christianj98.primarycustomerbase.dto.OrderDto;
import com.github.christianj98.primarycustomerbase.entity.Order;
import com.github.christianj98.primarycustomerbase.mapper.OrderMapperService;
import com.github.christianj98.primarycustomerbase.repository.OrderRepository;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.AMOUNT;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.ORDER_DATE;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.createOrder;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.createOrderDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
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
    private Order order;
    private OrderDto orderDto;

    @BeforeEach
    public void init() {
        order = createOrder(ORDER_DATE, AMOUNT);
        orderDto = createOrderDto(ORDER_DATE, AMOUNT);
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

}
