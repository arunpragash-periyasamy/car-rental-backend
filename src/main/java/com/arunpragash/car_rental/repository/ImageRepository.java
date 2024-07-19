package com.arunpragash.car_rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arunpragash.car_rental.model.table.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
}
