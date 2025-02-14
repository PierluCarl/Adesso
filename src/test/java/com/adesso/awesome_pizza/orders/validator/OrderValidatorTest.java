package com.adesso.awesome_pizza.orders.validator;

import static com.adesso.awesome_pizza.orders.model.enumeration.OrderStatus.CANCELED;
import static com.adesso.awesome_pizza.orders.model.enumeration.OrderStatus.COMPLETED;
import static com.adesso.awesome_pizza.orders.model.enumeration.OrderStatus.IN_PROGRESS;
import static com.adesso.awesome_pizza.orders.model.enumeration.OrderStatus.NEW;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adesso.awesome_pizza.orders.exception.OrderValidationException;
import com.adesso.awesome_pizza.orders.model.Order;
import com.adesso.awesome_pizza.orders.model.enumeration.OrderStatus;
import com.adesso.awesome_pizza.orders.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
public class OrderValidatorTest {

    @InjectMocks
    private OrderValidator orderValidator;
    
    @Mock
    private OrderRepository orderRepository;

    @Test
    @DisplayName("Should pass validation when validating order creation with valid description")
    void Should_PassValidation_When_ValidatingOrderCreationWithValidDescription() {
        assertDoesNotThrow(() -> orderValidator.validateForCreation("Valid Description"));
    }
    
    @ParameterizedTest
	@NullSource
	@DisplayName("Should not create order when description is null")
	void Should_ThrowOrderValidationException_When_CreatingAndDescriptionIsNull(String description) {
		OrderValidationException exception = assertThrows(OrderValidationException.class, () -> orderValidator.validateForCreation(description));
		assertThat("Cannot create an order. A description must be provided", is(exception.getMessage()));
	}

	@ParameterizedTest
	@ValueSource(strings = {"", " "})
	@DisplayName("Should not create order when description is empty")
	void Should_ThrowOrderValidationException_When_CreatingAndDescriptionIsEmpty(String description) {
		String expectedError = "Cannot create an order. A description must be provided";

		OrderValidationException exception = assertThrows(OrderValidationException.class, () -> orderValidator.validateForCreation(description));
		assertThat(expectedError, is(exception.getMessage()));
	}

    @Test
    @DisplayName("Should pass validation when validating valid status transition to IN_PROGRESS")
    void Should_PassValidation_When_ValidatingValidStatusTransitionToInProgress() {
    	var order = Order.builder()
    			.id(1L)
    			.status(NEW)
    			.build();
    	when(this.orderRepository.existsByStatusAndIdNot(IN_PROGRESS, 1L)).thenReturn(false);
       
    	assertDoesNotThrow(() -> orderValidator.validateStatusTransition(order, IN_PROGRESS));
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names="NEW", mode=Mode.EXCLUDE)
    @DisplayName("Should fail validation when validating status transition to IN_PROGRESS and previous status is not NEW")
    void Should_ThrowOrderValidationException_When_ValidatingStatusTransitionToInProgress(OrderStatus status) {
    	var order = Order.builder()
    			.id(1L)
    			.status(status)
    			.build();
    	String expectedError = String.format("Cannot start working on order. Its status is %s", order.getStatus());
    	
    	OrderValidationException exception = assertThrows(OrderValidationException.class, () -> orderValidator.validateStatusTransition(order, IN_PROGRESS));
    	assertThat(expectedError, is(exception.getMessage()));
    }
    
    @Test
    @DisplayName("Should fail validation when validating status transition to IN_PROGRESS and another order is already IN_PROGRESS")
    void Should_ThrowOrderValidationException_When_ValidatingStatusTransitionToInProgressAndAnotherOrderIsAlreadyInProgress() {
    	var order = Order.builder()
    			.id(1L)
    			.status(NEW)
    			.build();
    	String expectedError = "Cannot start working on order. Another order is already in progress";
    	
    	when(this.orderRepository.existsByStatusAndIdNot(IN_PROGRESS, 1L)).thenReturn(true);
    	
    	OrderValidationException exception = assertThrows(OrderValidationException.class, () -> orderValidator.validateStatusTransition(order, IN_PROGRESS));
    	assertThat(expectedError, is(exception.getMessage()));
    }
    
    @Test
    @DisplayName("Should pass validation when validating valid status transition to COMPLETED")
    void Should_PassValidation_When_ValidatingValidStatusTransitionToCompleted() {
    	var order = Order.builder()
    			.id(1L)
    			.status(IN_PROGRESS)
    			.build();
       
    	assertDoesNotThrow(() -> orderValidator.validateStatusTransition(order, COMPLETED));
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names="IN_PROGRESS", mode=Mode.EXCLUDE)
    @DisplayName("Should fail validation when validating status transition to COMPLETED and previous status is not IN_PROGRESS")
    void Should_ThrowOrderValidationException_When_ValidatingStatusTransitionToCompleted(OrderStatus status) {
    	var order = Order.builder()
    			.id(1L)
    			.status(status)
    			.build();
    	String expectedError = String.format("Cannot complete order. Its status is %s", order.getStatus());
    	
    	OrderValidationException exception = assertThrows(OrderValidationException.class, () -> orderValidator.validateStatusTransition(order, COMPLETED));
    	assertThat(expectedError, is(exception.getMessage()));
    }
 
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names={"NEW", "IN_PROGRESS"}, mode=Mode.INCLUDE)
    @DisplayName("Should pass validation when validating valid status transition to CANCELED")
    void Should_PassValidation_When_ValidatingValidStatusTransitionToCanceled(OrderStatus status) {
    	var order = Order.builder()
    			.id(1L)
    			.status(IN_PROGRESS)
    			.build();
       
    	assertDoesNotThrow(() -> orderValidator.validateStatusTransition(order, COMPLETED));
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names={"NEW", "IN_PROGRESS"}, mode=Mode.EXCLUDE)
    @DisplayName("Should fail validation when validating status transition to CANCELED and previous status is not IN_PROGRESS nor NEW")
    void Should_ThrowOrderValidationException_When_ValidatingStatusTransitionToCanceled(OrderStatus status) {
    	var order = Order.builder()
    			.id(1L)
    			.status(status)
    			.build();
    	String expectedError = String.format("Cannot cancel order. It was already completed");
    	
    	OrderValidationException exception = assertThrows(OrderValidationException.class, () -> orderValidator.validateStatusTransition(order, CANCELED));
    	assertThat(expectedError, is(exception.getMessage()));
    }
}
