package com.arunpragash.car_rental.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.arunpragash.car_rental.model.requestModel.BookingRequest;
import com.arunpragash.car_rental.model.requestModel.BookingResponse;
import com.arunpragash.car_rental.model.table.*;
import com.arunpragash.car_rental.model.table.Transaction.TransactionStatus;
import com.arunpragash.car_rental.repository.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BillingRepository billingRepository;

    @Autowired
    private CardDetailsRepository cardDetailsRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    CarImagesRepository carImagesRepository;


    @Value("${app.url}")
    private String appUrl;

    public String createBooking(BookingRequest bookingRequest, HttpServletRequest request) {
        // Extract username from request attributes
        String userName = (String) request.getAttribute("userName");

        // Get user details
        User user = userRepository.findByUserName(userName);

        // Get car details
        Car car = carRepository.findById(bookingRequest.getCarId())
                .orElseThrow(() -> new RuntimeException("Car not found with id " + bookingRequest.getCarId()));

        // Create booking
        Booking booking = new Booking();
        booking.setTenant(user);
        booking.setCar(car);
        booking.setLessor(car.getUser());
        booking.setStartDate(bookingRequest.getLocationForm().getPickupDate().toLocalDate());
        booking.setStartTime(bookingRequest.getLocationForm().getPickupDate().toLocalTime());
        booking.setEndDate(bookingRequest.getLocationForm().getReturnDate().toLocalDate());
        booking.setEndTime(bookingRequest.getLocationForm().getReturnDate().toLocalTime());
        booking.setDeliveryLocation(bookingRequest.getLocationForm().getDeliveryLocation());
        booking.setReturnLocation(bookingRequest.getLocationForm().getReturnLocation());
        
        bookingRepository.save(booking);

        // Create billing
        Billing billing = new Billing();
        billing.setUser(user);
        billing.setFirstName(bookingRequest.getBillingForm().getFirstName());
        billing.setNoOfPersons(bookingRequest.getBillingForm().getNoOfPersons());
        billing.setDrivingLicenceNumber(bookingRequest.getBillingForm().getDrivingLicenceNumber());
        billingRepository.save(billing);

        // Create card details
        CardDetails cardDetails = new CardDetails();
        cardDetails.setUser(user);
        cardDetails.setCardNumber(bookingRequest.getBillingForm().getCardNumber());
        cardDetails.setNameOnCard(bookingRequest.getBillingForm().getCardHolderName());
        cardDetails.setCvv(Integer.parseInt(bookingRequest.getBillingForm().getCvv()));
        cardDetails.setExpiry(bookingRequest.getBillingForm().getExpiryMonth().toLocalDate());
        cardDetailsRepository.save(cardDetails);

        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setCard(cardDetails);
        transaction.setTransactionNumber(UUID.randomUUID().toString());
        transaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);

        // Create order
        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setTransaction(transaction);
        order.setBilling(billing);
        order.setBooking(booking);
        orderRepository.save(order);

        return order.getOrderId();
    }

    public List<BookingResponse> getUserBookings(HttpServletRequest request) {
        // Extract username from request attributes
        String userName = (String) request.getAttribute("userName");

        // Get user details
        User user = userRepository.findByUserName(userName);
        List<Booking> bookings = bookingRepository.findByTenant(user);

        // Convert bookings to BookingResponse
        return bookings.stream().map(this::convertToBookingResponse).collect(Collectors.toList());

    }
    
    private BookingResponse convertToBookingResponse(Booking booking) {


        String carImage = appUrl + "/api/cars/images/" + carImagesRepository.findByCarId(booking.getCar().getId()).get(0).getId();
        return new BookingResponse(
                booking.getId(),
                booking.getTenant().getId(),
                booking.getLessor().getId(),
                booking.getCar().getId().toString(),
                booking.getCar().getName(),
                        carImage,
                booking.getCar().getModel().getModelName(),
                booking.getStartDate(),
                booking.getStartTime(),
                booking.getEndDate(),
                booking.getEndTime(),
                booking.getDeliveryLocation(),
                booking.getReturnLocation());
    }
}
