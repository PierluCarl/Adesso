package com.adesso.awesome_pizza.orders.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.adesso.awesome_pizza.orders.mapper.OrderMapper;
import com.adesso.awesome_pizza.orders.service.OrderService;

@SpringBootTest
@Transactional
@Rollback
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OrderRestControllerTest {

	private final String BASE_URL = "/api/orders/v1/";

	@Autowired
	private MockMvc mockMvc;

	@Mock
	private OrderService orderService;

	@Mock
	private OrderMapper orderMapper;
	
	private String description;
	
	@BeforeEach
	void setup() {
		this.description = "test description";
	}

	@Test
	@DisplayName("Should create order when a valid description is provided")
	void Should_CreateOrder_When_DescriptionIsValid() throws Exception {
		mockMvc.perform(post(BASE_URL + "/create")
				.content(description)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value("1"))
				.andExpect(jsonPath("$.status").value("NEW"))
				.andReturn();
	}

	@Test
	@DisplayName("Should get an order by id when an order with the provided ID exists")
	void Should_GetOrderById_When_AnOrderWithTheProvidedIdExists() throws Exception {
		mockMvc.perform(post(BASE_URL + "/create")
				.content(description)
				.contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		mockMvc.perform(get(BASE_URL + "/{id}", 1L)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value("1"))
				.andReturn();
	}
	
	@Test
	@DisplayName("Should start working on first pending order when it exists")
	void Should_StartWorkingOnFirstPendingOrder_When_ItExists() throws Exception {
 
		mockMvc.perform(post(BASE_URL + "/create")
				 .content(description)
				 .contentType(MediaType.APPLICATION_JSON))
				 .andExpect(status().isOk());

		mockMvc.perform(put(BASE_URL + "/start_working")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.status").value("IN_PROGRESS"));
	}

	@Test
	@DisplayName("Should complete order when order is valid")
	void Should_CompleteOrder_When_OrderIsValid() throws Exception {
		
		mockMvc.perform(post(BASE_URL + "/create")
				.content(description)
				.contentType(MediaType.APPLICATION_JSON));

		mockMvc.perform(put(BASE_URL + "/start_working")
			   .contentType(MediaType.APPLICATION_JSON));
	   
		mockMvc.perform(put("/api/v1/orders/complete/1")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.status").value("COMPLETED"));
	}

	@Test
	@DisplayName("Should cancel order when ID is valid")
	void Should_CancelOrder_When_IdIsValid() throws Exception {
		mockMvc.perform(post(BASE_URL + "/create")
				.content(description)
				.contentType(MediaType.APPLICATION_JSON));

		mockMvc.perform(put(BASE_URL + "/start_working")
			   .contentType(MediaType.APPLICATION_JSON));

		mockMvc.perform(delete(BASE_URL + "/cancel/1")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
}
