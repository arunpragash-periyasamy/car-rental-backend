package com.arunpragash.car_rental.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.arunpragash.car_rental.model.requestModel.BookingRequest;
import com.arunpragash.car_rental.model.requestModel.BookingResponse;
import com.arunpragash.car_rental.service.BookingService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<String> createBooking(@RequestBody BookingRequest bookingRequest,
            HttpServletRequest request) {
        String orderId = bookingService.createBooking(bookingRequest, request);
        return ResponseEntity.ok(orderId);
    }



    @GetMapping
    public ResponseEntity<List<BookingResponse>> getUserBookings(HttpServletRequest request) {
        return ResponseEntity.ok(bookingService.getUserBookings(request));
    }
}
