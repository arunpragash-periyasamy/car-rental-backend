package com.arunpragash.car_rental.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

import com.arunpragash.car_rental.model.table.User;
import com.arunpragash.car_rental.service.EmailService;
import com.arunpragash.car_rental.service.JwtService;
import com.arunpragash.car_rental.service.UserService;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private EmailService eamilService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        try {
            userService.registerUser(user);
            eamilService.sendEmail(user.getEmail(), "Car Rental Registration", "Hi <h2>"+user.getName()+"</h2> <br>Thank you for registering with us :) Happy Journey :) !!!");
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User user) {
        try{
            Authentication authentication = authManager
                    .authenticate(new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));
                    if (authentication.isAuthenticated()) {
                User userData = userService.getUser(user.getUserName());
                String token = jwtService.generateToken(userData);
                Map<String, String> map = new HashMap<>();
                map.put("token", token);
                map.put("userName", user.getUserName());
                map.put("userType", userData.getUserType());
                return ResponseEntity.ok(map);
            }
                
        } catch (Exception e) {
           
        }
        Map<String, String> map = new HashMap<>();
        map.put("error", "Invalid username or password");
        return ResponseEntity.status(401).body(map);
        }

}
