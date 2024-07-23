package com.arunpragash.car_rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arunpragash.car_rental.model.table.Address;

public interface AddressRepository extends JpaRepository<Address, Long>{

    Address findByCarId(Long id);
    
}
