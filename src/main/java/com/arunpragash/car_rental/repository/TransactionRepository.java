package com.arunpragash.car_rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arunpragash.car_rental.model.table.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long>{


}
