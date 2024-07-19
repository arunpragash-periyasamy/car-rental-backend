package com.arunpragash.car_rental.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.arunpragash.car_rental.model.requestModel.CarRequest;
import com.arunpragash.car_rental.model.table.Car;
import com.arunpragash.car_rental.model.table.CarImages;
import com.arunpragash.car_rental.model.table.CarModel;
import com.arunpragash.car_rental.model.table.CarPrice;
import com.arunpragash.car_rental.model.table.CarSpecs;
import com.arunpragash.car_rental.repository.CarImagesRepository;
import com.arunpragash.car_rental.repository.CarModelRepository;
import com.arunpragash.car_rental.repository.CarPriceRepository;
import com.arunpragash.car_rental.repository.CarRepository;
import com.arunpragash.car_rental.repository.CarSpecsRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class CarService {

    // Directory where uploads will be stored
    private static final String UPLOAD_DIR = "/uploads/";

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarModelRepository carModelRepository;

    @Autowired
    private CarSpecsRepository carSpecsRepository;

    @Autowired
    private CarPriceRepository carPriceRepository;

    @Autowired
    private CarImagesRepository carImagesRepository;

    public void saveCar(CarRequest carRequest, List<MultipartFile> images) throws IOException {
        // Save CarModel
        CarModel carModel = new CarModel();
        carModel.setBrandName(carRequest.getBrandName());
        carModel.setModelName(carRequest.getModelName());
        carModel.setBody(carRequest.getBody());
        carModel.setSeats(carRequest.getSeats());
        carModel = carModelRepository.save(carModel);

        // Save Car
        Car car = new Car();
        car.setName(carRequest.getCarName());
        car.setVin(carRequest.getVin());
        car.setYear(carRequest.getYear());
        car.setModel(carModel);
        car = carRepository.save(car);

        // Save CarSpecs
        CarSpecs carSpecs = new CarSpecs();
        carSpecs.setCar(car);
        carSpecs.setGearType(carRequest.getGearType());
        carSpecs.setMileage(carRequest.getMileage());
        carSpecs.setFuelType(carRequest.getFuelType());
        carSpecs.setDrivetrain(carRequest.getDrivetrain());
        carSpecs.setEnginePower(carRequest.getEnginePower());
        carSpecs.setBrake(carRequest.getBrake());
        carSpecsRepository.save(carSpecs);

        // Save CarPrice
        CarPrice carPrice = new CarPrice();
        carPrice.setCar(car);
        carPrice.setAmount(carRequest.getAmount());
        carPrice.setDoorDeliveryAndPickup(carRequest.getDoorDeliveryPrice());
        carPrice.setTripProtectionFees(carRequest.getTripProtectionFee());
        carPrice.setTax(carRequest.getTax());
        carPrice.setConvenienceFees(carRequest.getConvenienceFee());
        carPrice.setRefundableDeposit(carRequest.getRefundableDeposit());
        carPriceRepository.save(carPrice);

        // Save Images
        saveImages(car, images);
    }

    private void saveImages(Car car, List<MultipartFile> images) throws IOException {
        for (MultipartFile file : images) {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = fileName.substring(fileName.lastIndexOf("."));
            String generatedFileName = UUID.randomUUID().toString() + fileExtension;
            String uploadDir = System.getProperty("user.dir") + UPLOAD_DIR;

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(generatedFileName);
            Files.copy(file.getInputStream(), filePath);

            // Save image path in the database
            CarImages carImages = new CarImages();
            carImages.setCar(car);
            carImages.setPath(UPLOAD_DIR + generatedFileName); // Store relative path
            carImagesRepository.save(carImages);
        }
    }
}
