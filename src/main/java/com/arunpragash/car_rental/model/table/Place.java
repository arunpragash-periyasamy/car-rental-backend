package com.arunpragash.car_rental.model.table;



import java.sql.Timestamp;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.Data;

@Entity
@Data
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String pincode;
    private String state;
    private String city;
    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;
  @PrePersist
    protected void onCreate() {
        createdAt = Timestamp.from(Instant.now());
    }
}
