package com.arunpragash.car_rental.model.requestModel;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private int id;
    private Long userId;
    private Long tenantId;
    private String carId;
    private String carName;
    private String modelName;
    private LocalDate startDate;
    private LocalTime startTime;
    private LocalDate endDate;
    private LocalTime endTime;
    private String deliveryLocation;
    private String returnLocation;
}
