package com.adesso.awesome_pizza.orders.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

	private final String BASE_URL = "/api/orders/v1";
	private final String DESCRIPTION = "test description";

	@Autowired
	private MockMvc mockMvc;

	@Mock
	private OrderService orderService;

	@Mock
	private OrderMapper orderMapper;
	
	@Test
	@DisplayName("Should create order when a valid description is provided")
	void Should_CreateOrder_When_DescriptionIsValid() throws Exception {
		mockMvc.perform(post(BASE_URL + "/create")
				.content(DESCRIPTION)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value("2"))
				.andExpect(jsonPath("$.status").value("NEW"))
				.andExpect(jsonPath("$.insertDate").isNotEmpty())
				.andExpect(jsonPath("$.updateDate").isEmpty());;
	}
	
	@Test
	@DisplayName("Should not create order when an empty description is provided")
	void Should_NotCreateOrder_When_DescriptionIsNotValid() throws Exception {
		mockMvc.perform(post(BASE_URL + "/create")
				.content(" ")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(500))
				.andExpect(content().string(containsString("Cannot create an order. A description must be provided")));
	}

	@Test
	@DisplayName("Should get an order by id when an order with the provided ID exists")
	void Should_GetOrderById_When_AnOrderWithTheProvidedIdExists() throws Exception {
		mockMvc.perform(post(BASE_URL + "/create")
				.content(DESCRIPTION)
				.contentType(MediaType.APPLICATION_JSON));

		mockMvc.perform(get(BASE_URL + "/1")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@Test
	@DisplayName("Should not get an order by id when no order with the provided ID exists")
	void Should_NotGetOrderById_When_NoOrderWithTheProvidedIdExists() throws Exception {
		mockMvc.perform(get(BASE_URL + "/1")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(404))
				.andExpect(content().string(containsString("No order found with id 1")));
	}
	
	@Test
	@DisplayName("Should start working on first pending order when it exists")
	void Should_StartWorkingOnFirstPendingOrder_When_ItExists() throws Exception {
		mockMvc.perform(post(BASE_URL + "/create")
				 .content(DESCRIPTION)
				 .contentType(MediaType.APPLICATION_JSON));

		mockMvc.perform(put(BASE_URL + "/start_working")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("IN_PROGRESS"))
				.andExpect(jsonPath("$.insertDate").isNotEmpty())
				.andExpect(jsonPath("$.updateDate").isNotEmpty());
	}

	@Test
	@DisplayName("Should complete order when order is valid")
	void Should_CompleteOrder_When_OrderIsValid() throws Exception {
		mockMvc.perform(post(BASE_URL + "/create")
				.content(DESCRIPTION)
				.contentType(MediaType.APPLICATION_JSON));

		mockMvc.perform(put(BASE_URL + "/start_working")
			   .contentType(MediaType.APPLICATION_JSON));
	   
		mockMvc.perform(put(BASE_URL + "/complete/1")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("COMPLETED"))
				.andExpect(jsonPath("$.insertDate").isNotEmpty())
				.andExpect(jsonPath("$.updateDate").isNotEmpty());
	}

	@Test
	@DisplayName("Should cancel order when order is in valid status")
	void Should_CancelOrder_When_OrderIsNotCompleted() throws Exception {
		mockMvc.perform(post(BASE_URL + "/create")
				.content(DESCRIPTION)
				.contentType(MediaType.APPLICATION_JSON));

		mockMvc.perform(put(BASE_URL + "/start_working")
			   .contentType(MediaType.APPLICATION_JSON));

		mockMvc.perform(delete(BASE_URL + "/cancel/1")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@Test
	@DisplayName("Should not cancel order when order is already completed")
	void Should_NotCancelOrder_When_OrderIsCompleted() throws Exception {
		mockMvc.perform(post(BASE_URL + "/create")
				.content(DESCRIPTION)
				.contentType(MediaType.APPLICATION_JSON));

		mockMvc.perform(put(BASE_URL + "/start_working")
			   .contentType(MediaType.APPLICATION_JSON));

		mockMvc.perform(put(BASE_URL + "/complete/1")
				.contentType(MediaType.APPLICATION_JSON));
		
		mockMvc.perform(delete(BASE_URL + "/cancel/1")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(500))
				.andExpect(content().string(containsString("Cannot cancel order. It was already completed")));
	}
}
