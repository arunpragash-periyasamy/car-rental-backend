package com.arunpragash.car_rental.service;

import org.springframework.stereotype.Service;

import com.arunpragash.car_rental.model.Cars;
import com.arunpragash.car_rental.model.Test;
import com.arunpragash.car_rental.model.User;

@Service
public class TestService {
    public Test testing(Test test) {
        User user = test.getUser();
        Cars cars = test.getCars();
        System.out.println("\n\n\n\n\n\n\n");
        System.out.println(user);
        System.out.println(cars);
        System.out.println("\n\n\n\n\n\n\n");
        return test;
    }
}
