package com.arunpragash.car_rental.service;

import com.arunpragash.car_rental.model.User;
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

    // Add more methods as needed, e.g., find user by username, update user, delete
    // user, etc.
}
