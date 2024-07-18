package com.arunpragash.car_rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arunpragash.car_rental.model.Car;

public interface CarRepository extends JpaRepository<Car, Integer> {
}
