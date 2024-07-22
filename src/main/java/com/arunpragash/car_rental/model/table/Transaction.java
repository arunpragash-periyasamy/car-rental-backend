package com.arunpragash.car_rental.model.table;

import java.sql.Timestamp;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.Data;

@Entity
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "card_id")
    private CardDetails card;

    private String transactionNumber;
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;
  @PrePersist
    protected void onCreate() {
        createdAt = Timestamp.from(Instant.now());
    }
    // Getters and Setters
    public enum TransactionStatus {
        SUCCESS, FAIL
    }
}

