package com.arunpragash.car_rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arunpragash.car_rental.model.CarPrice;

public interface CarPriceRepository extends JpaRepository<CarPrice, Integer> {
}
