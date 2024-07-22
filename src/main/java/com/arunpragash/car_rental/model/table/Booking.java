package com.arunpragash.car_rental.model.table;

import java.sql.Date;
import java.sql.Time;
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
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private User tenant;

    @ManyToOne
    @JoinColumn(name = "lessor_id")
    private User lessor;

    private Date startDate;
    private Time startTime;
    private Date endDate;
    private Time endTime;
@Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
private Timestamp createdAt;
      @PrePersist
    protected void onCreate() {
        createdAt = Timestamp.from(Instant.now());
    }
}
