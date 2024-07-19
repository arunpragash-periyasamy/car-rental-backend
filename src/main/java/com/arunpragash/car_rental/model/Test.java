package com.arunpragash.car_rental.model;


import org.springframework.beans.factory.annotation.Autowired;

import com.arunpragash.car_rental.model.table.Cars;
import com.arunpragash.car_rental.model.table.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Test {
    int id;
    @Autowired
    Cars cars;
    @Autowired
    User user;
}
