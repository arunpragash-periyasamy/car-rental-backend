package com.arunpragash.car_rental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.arunpragash.car_rental.model.requestModel.CarRequest;
import com.arunpragash.car_rental.service.CarService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
@CrossOrigin("*")
public class CarController {

    @Autowired
    private CarService carService;

    @PostMapping
    public ResponseEntity<String> createCar(@ModelAttribute CarRequest carRequest,
            @RequestParam("images") List<MultipartFile> images) throws IOException {
        carService.saveCar(carRequest, images);
        return ResponseEntity.ok("Car created successfully");
    }
}
