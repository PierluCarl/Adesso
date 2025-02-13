package com.adesso.awesome_pizza.orders.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.adesso.awesome_pizza.orders.dto.OrderDTO;
import com.adesso.awesome_pizza.orders.model.Order;

@Component
public class OrderMapper {
	
	public List<OrderDTO> toDTOs(List<Order> orders) {
		return orders.stream()
				.map(this::toDTO)
				.toList();
	}
	
	public OrderDTO toDTO(Order order) {
		var dto = new OrderDTO();
		dto.id = order.getId();
		dto.description = order.getDescription();
		dto.status = order.getStatus();
		dto.insertDate = order.getInsertDate();
		dto.updateDate = order.getUpdateDate();
		return dto;
	}

}
