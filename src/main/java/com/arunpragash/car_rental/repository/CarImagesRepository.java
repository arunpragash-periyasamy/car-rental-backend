package com.arunpragash.car_rental.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arunpragash.car_rental.model.table.CarImages;





public interface CarImagesRepository extends JpaRepository<CarImages, Integer> {
    List<CarImages> findByCarId(Long carId);

    CarImages findById(Long Id);
}
