package com.adesso.awesome_pizza.orders.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.adesso.awesome_pizza.orders.model.Order;
import com.adesso.awesome_pizza.orders.model.enumeration.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {
	
	List<Order> findByStatusOrderByInsertDateAsc(OrderStatus status);
	
	Optional<Order> findFirstByStatusOrderByInsertDateAsc(OrderStatus status);
	
    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END FROM Order o WHERE o.status = :status AND o.id != :id")
    boolean existsByStatusAndIdNot(@Param("status") OrderStatus status, @Param("id") Long id);
	
}

