package com.arunpragash.car_rental.controller;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

import com.arunpragash.car_rental.model.requestModel.CarRequest;
import com.arunpragash.car_rental.model.requestModel.CarResponse;
import com.arunpragash.car_rental.model.table.Car;
import com.arunpragash.car_rental.service.CarService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cars")
@CrossOrigin("*")
public class CarController {

    @Autowired
    private CarService carService;

    @PostMapping
    public ResponseEntity<String> createCar(@ModelAttribute CarRequest carRequest,
            @RequestParam("images") List<MultipartFile> images, HttpServletRequest request) throws IOException {
        String userName = (String) request.getAttribute("userName");
        carService.saveCar(carRequest, images, userName);
        return ResponseEntity.ok("Car created successfully");
    }
    
    @GetMapping
    public ResponseEntity<List<CarResponse>> getAllCars() {
        List<CarResponse> carResponses = carService.getAllCars();
        return ResponseEntity.ok(carResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarResponse> getCar(@PathVariable Long id) {
        try {
            CarResponse car = carService.getCar(id);
            return ResponseEntity.ok(car);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


     @GetMapping("/images/{id}")
    public ResponseEntity<ByteArrayResource> getImageById(@PathVariable Long id) {
        try {
            CarService.ImageData imageData = carService.getImageById(id);
            ByteArrayResource resource = new ByteArrayResource(imageData.getData());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"image\"")
                    .contentType(org.springframework.http.MediaType.parseMediaType(imageData.getMimeType()))
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
