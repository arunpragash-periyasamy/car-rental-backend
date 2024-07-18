package com.arunpragash.car_rental.model;


import org.springframework.beans.factory.annotation.Autowired;

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
