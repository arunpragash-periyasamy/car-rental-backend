package com.arunpragash.car_rental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.arunpragash.car_rental.model.Test;
import com.arunpragash.car_rental.service.TestService;


@RestController
public class TestController {
    @Autowired
    TestService service;
    @GetMapping
    public Test test(@RequestBody Test test) {
        System.out.println("Welcome to the controller");
        service.testing(test);
        return new Test();
    }
    
    @PostMapping("/")
    public String testing() {
        return "Hello World";
    }
}
