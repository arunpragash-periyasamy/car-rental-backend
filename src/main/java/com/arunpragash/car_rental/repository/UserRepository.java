package com.arunpragash.car_rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arunpragash.car_rental.model.table.Car;
import com.arunpragash.car_rental.model.table.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(Long phoneNumber);

    User findByUserName(String userName);

    User findByUserNameOrEmailAndPassword(String username, String email, String password);
    // Add more custom query methods as needed

    User findByUserNameOrEmail(String userName, String email);

    User getIdByUserName(String userName);
}
