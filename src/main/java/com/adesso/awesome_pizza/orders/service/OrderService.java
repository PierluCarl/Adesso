package com.adesso.awesome_pizza.orders.service;

import java.util.List;

import com.adesso.awesome_pizza.orders.model.Order;

public interface OrderService {

	Order create(String description);

	Order getById(Long id);

	List<Order> getPendings();

	Order startWorking();
	
	Order complete(Long id);

	void cancel(Long id);

}
