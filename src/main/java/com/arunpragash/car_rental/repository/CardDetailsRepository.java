package com.arunpragash.car_rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arunpragash.car_rental.model.table.CardDetails;

public interface CardDetailsRepository extends JpaRepository<CardDetails, Long> {

}
