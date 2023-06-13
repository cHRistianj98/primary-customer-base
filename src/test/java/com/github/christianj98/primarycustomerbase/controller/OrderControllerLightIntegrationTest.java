package com.github.christianj98.primarycustomerbase.controller;

import com.github.christianj98.primarycustomerbase.dto.OrderCreateDto;
import com.github.christianj98.primarycustomerbase.dto.OrderDto;
import com.github.christianj98.primarycustomerbase.entity.Customer;
import com.github.christianj98.primarycustomerbase.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.FIRST_NAME;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.LAST_NAME;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.createCustomer;
import static com.github.christianj98.primarycustomerbase.utils.GlobalTestUtils.asJsonString;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.AMOUNT;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.ID;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.ORDERS_URI;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.ORDERS_URI_WITH_ID;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.ORDER_DATE;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.createOrderCreateDto;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.createOrderDto;
import static java.lang.String.valueOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test class for {@link OrderController}
 */
@WebMvcTest(OrderController.class)
public class OrderControllerLightIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private OrderService orderService;
    private OrderDto orderDto;
    private OrderCreateDto orderCreateDto;
    private Customer customer;

    @BeforeEach
    public void init() {
        int customerId = 1;
        orderDto = createOrderDto(ORDER_DATE, AMOUNT);
        orderCreateDto = createOrderCreateDto(ORDER_DATE, AMOUNT, customerId);
        customer = createCustomer(FIRST_NAME, LAST_NAME);
    }

    @Test
    @DisplayName("Find all orders together with information about the customer and the address")
    public void findAllOrders_AllExistingOrdersFound() throws Exception {
        // given
        when(orderService.findAll()).thenReturn(List.of(orderDto));

        // when + then
        mockMvc.perform(get(ORDERS_URI).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].date").value(orderDto.getDate().toString()))
                .andExpect(jsonPath("$[0].amount").value(orderDto.getAmount().toString()))
                .andExpect(jsonPath("$[0].customerDto.firstName").value(orderDto.getCustomerDto().getFirstName()))
                .andExpect(jsonPath("$[0].customerDto.lastName").value(orderDto.getCustomerDto().getLastName()));
    }

    @Test
    @DisplayName("Save order for existing customer but customer does not exist")
    public void saveOrder_CustomerNotExist() throws Exception {
        // given
        when(orderService.saveOrder(any())).thenThrow(EntityNotFoundException.class);

        // when + then
        mockMvc.perform(post(ORDERS_URI)
                        .contentType(APPLICATION_JSON)
                        .content(asJsonString(orderCreateDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Create order for existing customer")
    public void saveOrder_customerExist_OrderCreated() throws Exception {
        // given
        when(orderService.saveOrder(any())).thenReturn(orderDto);

        // when
        mockMvc.perform(post(ORDERS_URI)
                        .contentType(APPLICATION_JSON)
                        .content(asJsonString(orderCreateDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.date").value(orderCreateDto.getDate().toString()))
                .andExpect(jsonPath("$.amount").value(orderCreateDto.getAmount().toString()));

    }

    @Test
    @DisplayName("Find order by id")
    public void findOrderById_orderFound() throws Exception {
        // given
        when(orderService.findById(ID)).thenReturn(orderDto);

        // when + then
        mockMvc.perform(get(ORDERS_URI_WITH_ID, ID)
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(valueOf(orderDto.getAmount())))
                .andExpect(jsonPath("$.date").value(valueOf(orderDto.getDate())))
                .andExpect(jsonPath("$.customerDto.firstName").value(customer.getFirstName()))
                .andExpect(jsonPath("$.customerDto.lastName").value(customer.getLastName()));
    }

    @Test
    @DisplayName("Find order by id but order does not exist")
    public void findOrderById_orderNotFound() throws Exception {
        // given
        when(orderService.findById(ID)).thenThrow(EntityNotFoundException.class);

        // when + then
        mockMvc.perform(get(ORDERS_URI_WITH_ID, ID)
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

}
