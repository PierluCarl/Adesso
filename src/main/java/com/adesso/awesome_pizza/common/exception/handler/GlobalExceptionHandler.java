package com.adesso.awesome_pizza.common.exception.handler;


import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.adesso.awesome_pizza.common.exception.NotFoundException;
import com.adesso.awesome_pizza.orders.exception.OrderValidationException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<RestError> handleNotFoundException(NotFoundException ex) {
		return new ResponseEntity<>(restError(ex), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(OrderValidationException.class)
	public ResponseEntity<RestError> handleOrderValidationException(OrderValidationException ex) {
		return new ResponseEntity<>(restError(ex), HttpStatus.INTERNAL_SERVER_ERROR);
	}


	private RestError restError(Throwable t) {
		return new RestError(t.getClass().getSimpleName(), t.getMessage());
	}
	
	class RestError {

		private LocalDateTime timestamp;
		private String exception;
		private String message;

		public RestError(String exceptionName, String message) {
			this.timestamp = LocalDateTime.now();
			this.exception = exceptionName;
			this.message = message;
		}

		public LocalDateTime getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(LocalDateTime timestamp) {
			this.timestamp = timestamp;
		}

		public String getException() {
			return exception;
		}

		public void setException(String exception) {
			this.exception = exception;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

	}

}
