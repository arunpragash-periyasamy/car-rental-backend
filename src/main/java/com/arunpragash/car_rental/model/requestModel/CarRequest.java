package com.arunpragash.car_rental.model.requestModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarRequest {
    private Long id;
    private String carName;
    private String brandName;
    private String modelName;
    private String body;
    private String vin;
    private Integer year;
    private Integer seats;
    private String gearType;
    private String mileage;
    private String fuelType;
    private String drivetrain;
    private String enginePower;
    private String brake;
    private Integer amount;
    private Integer doorDeliveryPrice;
    private Integer tripProtectionFee;
    private Integer tax;
    private Integer convienenceFee;
    private Integer refundableDeposit;
    private String Address;
    private String state;
    private String city;
    private Integer pinCode;
    private String country;
    private List<MultipartFile> images;
}

