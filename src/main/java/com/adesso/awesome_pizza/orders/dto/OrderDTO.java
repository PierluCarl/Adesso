package com.adesso.awesome_pizza.orders.dto;

import java.time.LocalDateTime;

import com.adesso.awesome_pizza.orders.model.enumeration.OrderStatus;

public class OrderDTO {
	
	public Long id;
    public String description;
    public OrderStatus status;
    public LocalDateTime insertDate;
    public LocalDateTime updateDate;
}
