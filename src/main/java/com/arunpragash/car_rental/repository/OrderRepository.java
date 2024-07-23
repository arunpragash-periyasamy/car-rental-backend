package com.arunpragash.car_rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arunpragash.car_rental.model.table.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
