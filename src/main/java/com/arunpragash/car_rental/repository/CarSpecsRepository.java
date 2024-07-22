package com.arunpragash.car_rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arunpragash.car_rental.model.table.CarSpecs;

public interface CarSpecsRepository extends JpaRepository<CarSpecs, Integer> {

    CarSpecs findByCarId(Long id);
}
