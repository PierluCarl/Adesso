package com.adesso.awesome_pizza.orders.dto;

import java.time.LocalDateTime;

import com.adesso.awesome_pizza.orders.model.enumeration.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

public class OrderDTO {
	
	public Long id;
    public String description;
    public OrderStatus status;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    public LocalDateTime insertDate;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    public LocalDateTime updateDate;
}
