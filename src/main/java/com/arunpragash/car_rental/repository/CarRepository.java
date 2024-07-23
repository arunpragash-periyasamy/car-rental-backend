package com.arunpragash.car_rental.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arunpragash.car_rental.model.table.Car;
import com.arunpragash.car_rental.model.table.User;

public interface CarRepository extends JpaRepository<Car, Long> {

    List<Car> findAllByUser(User user);
   
}
