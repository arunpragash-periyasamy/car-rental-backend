package com.arunpragash.car_rental.model.table;

import java.sql.Timestamp;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import lombok.Data;

@Entity
@Data
public class CarSpecs {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "car_id")
    private Car car;

    private String gearType;
    private String mileage;
    private String fuelType;
    private String drivetrain;
    private String enginePower;
    private String transmission;
    private String brake;
@Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;
      @PrePersist
    protected void onCreate() {
        createdAt = Timestamp.from(Instant.now());
    }
}
