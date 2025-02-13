package com.adesso.awesome_pizza.orders.exception;

public class OrderValidationException extends RuntimeException {

	private static final long serialVersionUID = 2796954604790747377L;
	
	public OrderValidationException(String message) {
		super(message);
	}

}
