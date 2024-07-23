package com.arunpragash.car_rental.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arunpragash.car_rental.model.table.Booking;
import com.arunpragash.car_rental.model.table.User;

public interface BookingRepository extends JpaRepository<Booking, Long>{

    List<Booking> findByTenant(User user);

}
