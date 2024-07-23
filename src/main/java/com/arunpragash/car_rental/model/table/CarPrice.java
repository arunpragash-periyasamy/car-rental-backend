package com.arunpragash.car_rental.model.table;



import java.sql.Timestamp;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.Data;

@Entity
@Data
public class CarPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    private Integer amount;
    private String priceType;
    private Integer doorDeliveryAndPickup;
    private Integer tripProtectionFees;
    private Integer convenienceFee;
    private Integer tax;
    private Integer refundableDeposit;
    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;
   @PrePersist
    protected void onCreate() {
        createdAt = Timestamp.from(Instant.now());
    }
}
   
