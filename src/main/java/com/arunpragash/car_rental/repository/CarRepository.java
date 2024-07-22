package com.arunpragash.car_rental.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arunpragash.car_rental.model.table.Car;

public interface CarRepository extends JpaRepository<Car, Long> {
   
}
