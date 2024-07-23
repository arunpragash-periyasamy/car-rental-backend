package com.arunpragash.car_rental.service;

import com.arunpragash.car_rental.model.table.User;
import com.arunpragash.car_rental.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder; // BCrypt password encoder

    public User registerUser(User user) throws Exception {
        // Check if email is already registered
        Optional<User> existingUserByEmail = userRepository.findByEmail(user.getEmail());
        if (existingUserByEmail.isPresent()) {
            throw new Exception("Email already exists");
        }

        // Check if phone number is already registered
        Optional<User> existingUserByPhone = userRepository.findByPhoneNumber(user.getPhoneNumber());
        if (existingUserByPhone.isPresent()) {
            throw new Exception("Phone number already exists");
        }

        // Hash the password
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        // Save user to database
        return userRepository.save(user);
    }


    public User updateUser(Integer id, User updatedUser) throws Exception {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();

            // Update fields that are allowed to be updated
            existingUser.setName(updatedUser.getName());
            existingUser.setUserName(updatedUser.getUserName());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
            existingUser.setUserType(updatedUser.getUserType());

            // Save updated user to database
            return userRepository.save(existingUser);
        } else {
            throw new Exception("User not found with id: " + id);
        }
    }

    public User getUser(String userName) {
        User user = userRepository.findByUserName(userName);
        return user;
    }

    public Long getUserId(String userName) {
        Long id = userRepository.getIdByUserName(userName).getId();
        System.out.println(id);
        return id;
    }
}
// User(id=1,name=Arunpragash,userName=arun_racer,email=arunpragash46 @gmail.com,phoneNumber=6382868122,password=$2a$10$CnMKsNaVLfL3XLClmC0nb.iaiKX6ekUYJ6AMFEiinR96ZW0KMP2xe,userType=admin,createdAt=2024-07-18 18:29:03.0)