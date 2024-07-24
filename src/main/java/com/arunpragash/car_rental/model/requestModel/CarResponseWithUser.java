package com.arunpragash.car_rental.model.requestModel;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarResponseWithUser extends CarResponse{
    public CarResponseWithUser(Long id, String name, String brandName, String modelName, String body, String vin,
            Integer year, Integer seats, String gearType, String mileage, String fuelType, String drivetrain,
            String enginePower, String brake, Integer amount, Integer doorDeliveryAndPickup, Integer tripProtectionFees,
            Integer tax, Integer convenienceFee, Integer refundableDeposit, String address, String city, String state,
            Integer pinCode, String country, List<String> images, String userName2, String email2, Long phoneNumber2) {
    }
    private String userName;
    private String email;
    private Long phoneNumber;
}
