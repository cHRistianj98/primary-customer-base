package com.github.christianj98.primarycustomerbase.controller;

import com.github.christianj98.primarycustomerbase.dto.OrderCreateDto;
import com.github.christianj98.primarycustomerbase.dto.OrderDto;
import com.github.christianj98.primarycustomerbase.dto.OrderUpdateDto;
import com.github.christianj98.primarycustomerbase.entity.Customer;
import com.github.christianj98.primarycustomerbase.entity.Order;
import com.github.christianj98.primarycustomerbase.repository.CustomerRepository;
import com.github.christianj98.primarycustomerbase.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.FIRST_NAME;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.LAST_NAME;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.createCustomer;
import static com.github.christianj98.primarycustomerbase.utils.GlobalTestUtils.asJsonString;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.AMOUNT;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.ID;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.ORDERS_URI;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.ORDERS_URI_WITH_ID;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.ORDER_DATE;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.createOrder;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.createOrderCreateDto;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.createOrderDto;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.createOrderUpdateDto;
import static java.lang.String.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("integration")
public class OrderControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private OrderRepository orderRepository;

    private OrderDto orderDto;
    private Order order;
    private OrderCreateDto orderCreateDto;
    private Customer customer;
    private OrderUpdateDto orderUpdateDto;

    @BeforeEach
    public void init() {
        int customerId = 1;
        orderDto = createOrderDto(ORDER_DATE, AMOUNT);
        orderCreateDto = createOrderCreateDto(ORDER_DATE, AMOUNT, customerId);
        customer = createCustomer(FIRST_NAME, LAST_NAME);
        order = createOrder(ORDER_DATE, AMOUNT);
        orderUpdateDto = createOrderUpdateDto();
    }

    @Test
    @DisplayName("Find all orders together with information about the customer and the address")
    public void findAllOrders_AllExistingOrdersFound() throws Exception {
        // when + then
        mockMvc.perform(get(ORDERS_URI).contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Save order for existing customer but customer does not exist")
    public void saveOrder_CustomerNotExist() throws Exception {
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
        customerRepository.save(customer);

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
        customerRepository.save(customer);
        orderRepository.save(order);

        // when + then
        mockMvc.perform(get(ORDERS_URI_WITH_ID, ID)
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(valueOf(order.getAmount())))
                .andExpect(jsonPath("$.date").value(valueOf(order.getDate())))
                .andExpect(jsonPath("$.customerDto.firstName").value(customer.getFirstName()))
                .andExpect(jsonPath("$.customerDto.lastName").value(customer.getLastName()));
    }

    @Test
    @DisplayName("Find order by id but order does not exist")
    public void findOrderById_orderNotFound() throws Exception {
        // when + then
        final String responseBody = mockMvc.perform(get(ORDERS_URI_WITH_ID, ID)
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(responseBody).isEqualTo("Order not found with given id: " + ID);
    }

    @Test
    @DisplayName("Update order with specific id")
    public void updateOrder_orderUpdatedSuccessfully() throws Exception {
        // given
        customerRepository.save(customer);
        orderRepository.save(order);

        // when + then
        mockMvc.perform(put(ORDERS_URI_WITH_ID, ID)
                        .contentType(APPLICATION_JSON)
                        .content(asJsonString(orderUpdateDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.amount").value(valueOf(orderUpdateDto.getAmount())))
                .andExpect(jsonPath("$.date").value(valueOf(orderUpdateDto.getDate())))
                .andExpect(jsonPath("$.customerDto.firstName").value(customer.getFirstName()))
                .andExpect(jsonPath("$.customerDto.lastName").value(customer.getLastName()));
    }

    @Test
    @DisplayName("Update order but order does not exist")
    public void updateOrder_orderNotFound() throws Exception {
        // given
        int id = 999;

        // when + then
        final String responseBody = mockMvc.perform(put(ORDERS_URI_WITH_ID, id)
                        .contentType(APPLICATION_JSON)
                        .content(asJsonString(orderUpdateDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse().getContentAsString();
        assertThat(responseBody).contains(valueOf(id));
    }

    @Test
    @DisplayName("Delete order but order was not found")
    public void deleteOrder_orderNotFound() throws Exception {
        // given
        int id = 999;

        // when + then
        final String responseBody = mockMvc.perform(delete(ORDERS_URI_WITH_ID, id)
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse().getContentAsString();
        assertThat(responseBody).contains(valueOf(id));
    }

    @Test
    @DisplayName("Delete order with specific id")
    public void deleteOrder_orderDeleteSuccessfully() throws Exception {
        // given
        customerRepository.save(customer);
        orderRepository.save(order);

        // when + then
        mockMvc.perform(delete(ORDERS_URI_WITH_ID, ID)
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

}
