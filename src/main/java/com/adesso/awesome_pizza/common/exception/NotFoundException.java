package com.adesso.awesome_pizza.common.exception;

public class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = -2729722983956828731L;

	public NotFoundException(String message) {
        super(message);
    }

}
