package com.adesso.awesome_pizza.orders.validator;

import static com.adesso.awesome_pizza.orders.model.enumeration.OrderStatus.CANCELED;
import static com.adesso.awesome_pizza.orders.model.enumeration.OrderStatus.COMPLETED;
import static com.adesso.awesome_pizza.orders.model.enumeration.OrderStatus.IN_PROGRESS;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.adesso.awesome_pizza.orders.exception.OrderValidationException;
import com.adesso.awesome_pizza.orders.model.Order;
import com.adesso.awesome_pizza.orders.model.enumeration.OrderStatus;
import com.adesso.awesome_pizza.orders.repository.OrderRepository;

@Component
public class OrderValidator {

	private final OrderRepository orderRepository;
	
	public OrderValidator(final OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}
	
	public void validateForCreation(String description) {
		if (StringUtils.isBlank(description)) {
			throw new OrderValidationException("Cannot create an order. A description must be provided");
		}
	}

	public void validateStatusTransition(Order order, OrderStatus newStatus) {
		switch (newStatus) {
			case IN_PROGRESS -> validateForWorkInProgress(order);
			case COMPLETED -> validateForCompletion(order);
			case CANCELED -> validateForCancelation(order);
			default -> throw new OrderValidationException("Invalid status");
		}
	}

	private void validateForCancelation(Order order) {
		if (!CANCELED.isPreviousStatusAllowed(order.getStatus())) {
			throw new OrderValidationException("Cannot cancel order. It was already completed");
		}
	}

	private void validateForCompletion(Order order) {
		if (!COMPLETED.isPreviousStatusAllowed(order.getStatus())) {
			throw new OrderValidationException(String.format("Cannot complete order. Its status is %s", order.getStatus()));
		}
	}

	private void validateForWorkInProgress(Order order) {
		if (!IN_PROGRESS.isPreviousStatusAllowed(order.getStatus())) {
			throw new OrderValidationException(String.format("Cannot start working on order. Its status is %s", order.getStatus()));
		}
		if (this.orderRepository.existsByStatusAndIdNot(IN_PROGRESS, order.getId())) {
			throw new OrderValidationException("Cannot start working on order. Another order is already in progress");
		}
	}

}
