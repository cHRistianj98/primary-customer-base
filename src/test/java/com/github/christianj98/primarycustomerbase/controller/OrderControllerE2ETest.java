package com.github.christianj98.primarycustomerbase.controller;

import com.github.christianj98.primarycustomerbase.dto.CustomerDto;
import com.github.christianj98.primarycustomerbase.dto.OrderCreateDto;
import com.github.christianj98.primarycustomerbase.dto.OrderDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.util.List;

import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.CUSTOMERS_URI;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.FIRST_NAME;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.LAST_NAME;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.createCustomerDto;
import static com.github.christianj98.primarycustomerbase.utils.GlobalTestUtils.HOST;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.AMOUNT;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.ORDERS_URI;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.ORDER_DATE;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.createOrderCreateDto;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.createOrderDto;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("e2e")
@ExtendWith(OutputCaptureExtension.class)
public class OrderControllerE2ETest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    private OrderDto orderDto;
    private OrderCreateDto orderCreateDto;
    private CustomerDto customerDto;

    @BeforeEach
    public void init() {
        int customerId = 1;
        orderDto = createOrderDto(ORDER_DATE, AMOUNT);
        orderCreateDto = createOrderCreateDto(ORDER_DATE, AMOUNT, customerId);
        customerDto = createCustomerDto(FIRST_NAME, LAST_NAME);
    }

    @Test
    @DisplayName("Find all orders together with information about the customer and the address")
    public void findAllOrders_AllExistingOrdersFound() {
        // when
        var response = restTemplate.getForEntity(ORDERS_URI, List.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    @DisplayName("Save order for existing customer but customer does not exist")
    public void saveOrder_CustomerNotExist(CapturedOutput output) {
        // given
        orderCreateDto.setCustomerId(999);

        // when
        var createdOrder = restTemplate.postForEntity(ORDERS_URI, orderCreateDto, String.class);

        // then
        assertThat(createdOrder.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(output).contains(Integer.toString(orderCreateDto.getCustomerId()));
    }

    @Test
    @DisplayName("Create order for existing customer")
    public void saveOrder_customerExist_OrderCreated() {
        // given
        restTemplate.postForEntity(CUSTOMERS_URI, customerDto, CustomerDto.class);
        orderCreateDto.setCustomerId(1);

        // when
        var response = restTemplate.postForEntity(ORDERS_URI, orderCreateDto, OrderDto.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        final OrderDto createdOrder = response.getBody();
        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.getDate()).isEqualTo(orderCreateDto.getDate());
        assertThat(createdOrder.getAmount()).isEqualTo(orderCreateDto.getAmount());
        assertThat(createdOrder.getCustomerDto().getFirstName()).isEqualTo(customerDto.getFirstName());
        assertThat(createdOrder.getCustomerDto().getLastName()).isEqualTo(customerDto.getLastName());
        final URI location = response.getHeaders().getLocation();
        assertThat(location).isNotNull();
        assertThat(location.getPath()).contains(ORDERS_URI);
        assertThat(location.getHost()).isEqualTo(HOST);
        assertThat(location.getPort()).isEqualTo(port);
    }
}
