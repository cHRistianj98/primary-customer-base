package com.github.christianj98.primarycustomerbase.controller;

import com.github.christianj98.primarycustomerbase.dto.CustomerDto;
import com.github.christianj98.primarycustomerbase.dto.OrderCreateDto;
import com.github.christianj98.primarycustomerbase.dto.OrderDto;
import com.github.christianj98.primarycustomerbase.dto.OrderUpdateDto;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.ID;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.ORDERS_URI;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.ORDERS_URI_WITH_ID;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.ORDER_DATE;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.createOrderCreateDto;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.createOrderDto;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.createOrderUpdateDto;
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
    private OrderUpdateDto orderUpdateDto;

    @BeforeEach
    public void init() {
        int customerId = 1;
        orderDto = createOrderDto(ORDER_DATE, AMOUNT);
        orderCreateDto = createOrderCreateDto(ORDER_DATE, AMOUNT, customerId);
        customerDto = createCustomerDto(FIRST_NAME, LAST_NAME);
        orderUpdateDto = createOrderUpdateDto();
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
        orderCreateDto.setCustomerId(ID);

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

    @Test
    @DisplayName("Find order by id")
    public void findOrderById_orderFound() {
        // given
        restTemplate.postForEntity(CUSTOMERS_URI, customerDto, CustomerDto.class);
        orderCreateDto.setCustomerId(ID);
        restTemplate.postForEntity(ORDERS_URI, orderCreateDto, OrderDto.class);

        // when
        var response = restTemplate.getForEntity(ORDERS_URI_WITH_ID, OrderDto.class, ID);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        final OrderDto foundOrder = response.getBody();
        assertThat(foundOrder.getOrderId()).isEqualTo(ID);
        assertThat(foundOrder.getDate()).isEqualTo(orderCreateDto.getDate());
        assertThat(foundOrder.getAmount()).isEqualTo(orderCreateDto.getAmount());
    }

    @Test
    @DisplayName("Find order by id but order does not exist")
    public void findOrderById_orderNotFound(CapturedOutput output) {
        // when
        var response = restTemplate.getForEntity(ORDERS_URI_WITH_ID, String.class, ID);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(output).contains(String.valueOf(ID));
        assertThat(response.getBody()).contains(String.valueOf(ID));
    }

    @Test
    @DisplayName("Update order with specific id")
    public void updateOrder_orderUpdatedSuccessfully() {
        // given
        restTemplate.postForEntity(CUSTOMERS_URI, customerDto, CustomerDto.class);
        orderCreateDto.setCustomerId(ID);
        restTemplate.postForEntity(ORDERS_URI, orderCreateDto, OrderDto.class);
        HttpEntity<OrderUpdateDto> requestEntity = createRequestEntity();

        // when
        var response = restTemplate.exchange(
                ORDERS_URI_WITH_ID,
                HttpMethod.PUT,
                requestEntity,
                OrderDto.class,
                ID
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        final OrderDto updatedOrder = response.getBody();
        assertThat(updatedOrder.getOrderId()).isEqualTo(ID);
        assertThat(updatedOrder.getDate()).isEqualTo(orderUpdateDto.getDate());
        assertThat(updatedOrder.getAmount()).isEqualTo(orderUpdateDto.getAmount());
    }

    @Test
    @DisplayName("Update order but order does not exist")
    public void updateOrder_orderNotFound(CapturedOutput output) {
        // given
        int id = 999;
        HttpEntity<OrderUpdateDto> requestEntity = createRequestEntity();

        // when
        var response = restTemplate.exchange(
                ORDERS_URI_WITH_ID,
                HttpMethod.PUT,
                requestEntity,
                String.class,
                id
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(output).contains(String.valueOf(id));
        assertThat(response.getBody()).contains(String.valueOf(id));
    }

    private HttpEntity<OrderUpdateDto> createRequestEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OrderUpdateDto> requestEntity = new HttpEntity<>(orderUpdateDto, headers);
        return requestEntity;
    }

}
