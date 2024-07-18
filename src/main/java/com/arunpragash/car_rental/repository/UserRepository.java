package com.arunpragash.car_rental.repository;

import com.arunpragash.car_rental.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(Long phoneNumber);

    // Add more custom query methods as needed
}
