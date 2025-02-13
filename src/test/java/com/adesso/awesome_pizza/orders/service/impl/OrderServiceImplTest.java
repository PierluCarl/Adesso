package com.adesso.awesome_pizza.orders.service.impl;

import static com.adesso.awesome_pizza.orders.model.enumeration.OrderStatus.CANCELED;
import static com.adesso.awesome_pizza.orders.model.enumeration.OrderStatus.COMPLETED;
import static com.adesso.awesome_pizza.orders.model.enumeration.OrderStatus.IN_PROGRESS;
import static com.adesso.awesome_pizza.orders.model.enumeration.OrderStatus.NEW;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adesso.awesome_pizza.common.exception.NotFoundException;
import com.adesso.awesome_pizza.orders.model.Order;
import com.adesso.awesome_pizza.orders.model.enumeration.OrderStatus;
import com.adesso.awesome_pizza.orders.repository.OrderRepository;
import com.adesso.awesome_pizza.orders.validator.OrderValidator;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

	@InjectMocks
	private OrderServiceImpl orderService;

	@Mock
	private OrderValidator orderValidator;

	@Mock
	private OrderRepository orderRepository;

	@Test
	@DisplayName("Should create order when description is provided")
	void Should_CreateOrder_When_DescriptionIsProvided() {
		String description = "Test Order";
	    doNothing().when(orderValidator).validateForCreation(description);

		Order order = assertDoesNotThrow(() -> orderService.create(description));
		verify(orderValidator).validateForCreation(description);
		verify(orderRepository).save(any(Order.class));
		assertNotNull(order.getInsertDate());
	}
	
	@Test
	@DisplayName("Should get order by ID when order with the provided ID exists")
	void Should_GetOrderById_When_OrderWithProvidedIdExists() {
		Order order = buildOrder(NEW);
		when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

		Order foundOrder = orderService.getById(1L);

		verify(orderRepository).findById(1L);
		assertThat(order, is(foundOrder));
	}
	
	@Test
	@DisplayName("Should get order by ID when no order with the provided ID exists")
	void Should_ThrowNotFoundException_When_NoOrderWithProvidedIdExists() {
		when(orderRepository.findById(1L)).thenReturn(Optional.empty());

		NotFoundException exception = assertThrows(NotFoundException.class, () ->  orderService.getById(1L));

		verify(orderRepository).findById(1L);
		assertThat("No order found with id 1", is(exception.getMessage()));
	}

	@Test
	@DisplayName("Should get pending orders when there are some")
	void Should_GetPendingOrders_When_ThereAreSome() {
		Order order = buildOrder(NEW);
		when(orderRepository.findByStatusOrderByInsertDateAsc(NEW)).thenReturn(List.of(order));

		List<Order> result = orderService.getPendings();

		verify(orderRepository).findByStatusOrderByInsertDateAsc(NEW);
		assertThat(result, hasSize(1));
	}

	@Test
	@DisplayName("Should start working on first pending order when it exists")
	void Should_StartWorkingOnFirstPendingOrder_When_ItExists() {
		Order order = buildOrder(NEW);
	    doNothing().when(orderValidator).validateStatusTransition(order, IN_PROGRESS);
		when(orderRepository.findFirstByStatusOrderByInsertDateAsc(NEW)).thenReturn(Optional.of(order));

		Order orderInProgress = orderService.startWorking();

		verify(orderRepository).findFirstByStatusOrderByInsertDateAsc(NEW);
		assertThat(IN_PROGRESS, is(orderInProgress.getStatus()));
	}

	@Test
	@DisplayName("Should complete order when order is valid")
	void Should_CompleteOrder_When_IdIsValid() {
		Order order = buildOrder(IN_PROGRESS);
		doNothing().when(orderValidator).validateStatusTransition(order, COMPLETED);
		when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

		Order completedOrder = orderService.complete(1L);

		verify(orderRepository).findById(1L);
		assertThat(COMPLETED, is(completedOrder.getStatus()));
	}

	@ParameterizedTest
	@EnumSource(value = OrderStatus.class, names = {"COMPLETED", "CANCELED"}, mode = Mode.EXCLUDE)
	@DisplayName("Should cancel order when ID is valid")
	void Should_CancelOrder_When_IdIsValid(OrderStatus status) {
		Order order = buildOrder(status);
		
		doNothing().when(orderValidator).validateStatusTransition(order, CANCELED);
		when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

		orderService.cancel(1L);

		verify(orderRepository).findById(1L);
	}
	
	private Order buildOrder(OrderStatus status) {
		return Order.builder()
				.id(1L)
				.description("Test Order")
				.status(status)
				.insertDate(LocalDateTime.now())
				.build();
	}
}
