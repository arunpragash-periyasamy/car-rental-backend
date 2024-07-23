package com.arunpragash.car_rental.model.requestModel;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingRequest {
    private LocationForm locationForm;
    private BillingForm billingForm;
    private Long carId;

    @Data
    public static class LocationForm {
        private String deliveryLocation;
        private String returnLocation;
        private String book;
        private LocalDateTime pickupDate;
        private LocalDateTime returnDate;
    }

    @Data
    public static class BillingForm {
        private String firstName;
        private int noOfPersons;
        private String street;
        private String country;
        private String city;
        private String pinCode;
        private String email;
        private String mobileNumber;
        private String drivingLicenceNumber;
        private String cardHolderName;
        private String cardNumber;
        private LocalDateTime expiryMonth;
        private String cvv;
    }
}
