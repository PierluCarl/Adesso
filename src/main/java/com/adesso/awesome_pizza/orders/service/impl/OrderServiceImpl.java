package com.adesso.awesome_pizza.orders.service.impl;

import static com.adesso.awesome_pizza.orders.model.enumeration.OrderStatus.CANCELED;
import static com.adesso.awesome_pizza.orders.model.enumeration.OrderStatus.COMPLETED;
import static com.adesso.awesome_pizza.orders.model.enumeration.OrderStatus.IN_PROGRESS;
import static com.adesso.awesome_pizza.orders.model.enumeration.OrderStatus.NEW;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adesso.awesome_pizza.common.exception.NotFoundException;
import com.adesso.awesome_pizza.orders.model.Order;
import com.adesso.awesome_pizza.orders.model.enumeration.OrderStatus;
import com.adesso.awesome_pizza.orders.repository.OrderRepository;
import com.adesso.awesome_pizza.orders.service.OrderService;
import com.adesso.awesome_pizza.orders.validator.OrderValidator;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
	
	private final OrderValidator orderValidator;
	private final OrderRepository orderRepository;
	
	public OrderServiceImpl(
			final OrderValidator orderValidator,
			final OrderRepository orderRepository
			) {
		this.orderValidator = orderValidator;
		this.orderRepository = orderRepository;
	}

	@Override
	public Order create(String description) {
		this.orderValidator.validateForCreation(description);
		
		Order newOrder = Order.builder()
				.description(description)
				.insertDate(LocalDateTime.now())
				.status(NEW)
				.build();
		return this.saveAndReturn(newOrder);
	}

	@Override
	public Order getById(Long id) {
		return this.doGetById(id);
	}

	@Override
	public List<Order> getPendings() {
		return this.orderRepository.findByStatusOrderByInsertDateAsc(NEW);
	}

	@Override
	public Order startWorking() {
		Order order = this.orderRepository.findFirstByStatusOrderByInsertDateAsc(NEW)
				.orElseThrow(() -> new NotFoundException("No new order found"));
		return manage(order, IN_PROGRESS);
	}

	@Override
	public Order complete(Long id) {
		Order order = this.doGetById(id);
		return this.manage(order, COMPLETED);
	}

	@Override
	public void cancel(Long id) {
		Order order = this.doGetById(id);
		this.manage(order, CANCELED);
	}
	
	private Order doGetById(Long id) {
		return this.orderRepository.findById(id)
				.orElseThrow(() -> new NotFoundException(String.format("No order found with id %s", id)));
	}
	
	private Order manage(Order order, OrderStatus newStatus) {
		this.orderValidator.validateStatusTransition(order, newStatus);
		order.setStatus(newStatus);
		order.setUpdateDate(LocalDateTime.now());
		return saveAndReturn(order);
	}
	
	private Order saveAndReturn(Order newOrder) {
		this.orderRepository.save(newOrder);
		return newOrder;
	}

}
