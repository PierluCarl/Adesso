package com.adesso.awesome_pizza.orders.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.adesso.awesome_pizza.orders.dto.OrderDTO;
import com.adesso.awesome_pizza.orders.mapper.OrderMapper;
import com.adesso.awesome_pizza.orders.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/orders/v1")
@Tag(name = "Orders", description = "API for Order Management")
public class OrderRestController {

	private final OrderService orderService;
	private final OrderMapper orderMapper;
	
	public OrderRestController(
			final OrderService orderService,
			final OrderMapper orderMapper) {
		this.orderService = orderService;
		this.orderMapper = orderMapper;
	}

	@PostMapping("/create")
	@Operation(description = "Create a new order") 
	public OrderDTO create(@RequestBody String description) {
		return this.orderMapper.toDTO(this.orderService.create(description));
	}

	@GetMapping("/{id}")
	@Operation(description = "Retrieves the order with the specified ID")
	public OrderDTO getOrderById(@PathVariable Long id) {
		return this.orderMapper.toDTO(this.orderService.getById(id));
	}

	@GetMapping("/pending")
	@Operation(description = "Retrieves all new orders")
	public List<OrderDTO> getPendingOrders() {
		return this.orderMapper.toDTOs(this.orderService.getPendings());
	}

	@PutMapping("/start_working")
	@Operation(description = "Start working on the first new order in the queue")
	public OrderDTO startWorking() {
		return this.orderMapper.toDTO(this.orderService.startWorking());
	}

	@PutMapping("/complete/{id}")
	@Operation(description = "Completes the order with the specified ID") 
	public OrderDTO complete(@PathVariable Long id) {
		return this.orderMapper.toDTO(this.orderService.complete(id));
	}

	@DeleteMapping("/cancel/{id}")
	@Operation(description = "Cancels the order with the specified ID")
	public void cancelOrder(@RequestParam Long id) {
		this.orderService.cancel(id);
	}

}
