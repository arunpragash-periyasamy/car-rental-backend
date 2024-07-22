package com.arunpragash.car_rental.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.arunpragash.car_rental.model.requestModel.CarRequest;
import com.arunpragash.car_rental.model.requestModel.CarResponse;
import com.arunpragash.car_rental.model.table.Car;
import com.arunpragash.car_rental.model.table.CarImages;
import com.arunpragash.car_rental.model.table.CarModel;
import com.arunpragash.car_rental.model.table.CarPrice;
import com.arunpragash.car_rental.model.table.CarSpecs;
import com.arunpragash.car_rental.model.table.User;
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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CarService {

    @Autowired
    UserService userService;

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

    public void saveCar(CarRequest carRequest, List<MultipartFile> images, String userName) throws IOException {
        // Save CarModel
        User user = userService.getUser(userName);
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
        car.setUser(user);
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

    public CarResponse getCar(Long id) {
        Optional<Car> carOptional = carRepository.findById(id);
        if (carOptional.isPresent()) {
            Car car = carOptional.get();
            CarModel carModel = car.getModel();
            CarSpecs carSpecs = carSpecsRepository.findByCarId(car.getId());
            CarPrice carPrice = carPriceRepository.findByCarId(car.getId());
            List<Long> images = carImagesRepository.findByCarId(car.getId())
                    .stream().map(CarImages::getId).collect(Collectors.toList());
            return new CarResponse(
                    car.getId(),
                    car.getName(),
                    carModel.getBrandName(),
                    carModel.getModelName(),
                    carModel.getBody(),
                    car.getVin(),
                    car.getYear(),
                    carModel.getSeats(),
                    carSpecs.getGearType(),
                    carSpecs.getMileage(),
                    carSpecs.getFuelType(),
                    carSpecs.getDrivetrain(),
                    carSpecs.getEnginePower(),
                    carSpecs.getBrake(),
                    carPrice.getAmount(),
                    carPrice.getDoorDeliveryAndPickup(),
                    carPrice.getTripProtectionFees(),
                    carPrice.getTax(),
                    carPrice.getConvenienceFees(),
                    carPrice.getRefundableDeposit(),
                    images);
        }
        return null;
    }
  
    public List<CarResponse> getAllCars() {
        return carRepository.findAll().stream().map(car -> {
            CarModel carModel = car.getModel();
            CarSpecs carSpecs = carSpecsRepository.findByCarId(car.getId());
            CarPrice carPrice = carPriceRepository.findByCarId(car.getId());
            List<Long> images = carImagesRepository.findByCarId(car.getId())
                    .stream().map(CarImages::getId).collect(Collectors.toList());

            return new CarResponse(
                    car.getId(),
                    car.getName(),
                    carModel.getBrandName(),
                    carModel.getModelName(),
                    carModel.getBody(),
                    car.getVin(),
                    car.getYear(),
                    carModel.getSeats(),
                    carSpecs.getGearType(),
                    carSpecs.getMileage(),
                    carSpecs.getFuelType(),
                    carSpecs.getDrivetrain(),
                    carSpecs.getEnginePower(),
                    carSpecs.getBrake(),
                    carPrice.getAmount(),
                    carPrice.getDoorDeliveryAndPickup(),
                    carPrice.getTripProtectionFees(),
                    carPrice.getTax(),
                    carPrice.getConvenienceFees(),
                    carPrice.getRefundableDeposit(),
                    images);
        }).collect(Collectors.toList());
    }
    

    public Optional<Car> getCarById(Long id) {
        return carRepository.findById(id);
    }


    public ImageData getImageById(Long id) throws IOException {
        CarImages carImages = carImagesRepository.findById(id);

        String imagePath = carImages.getPath();
        Path path = Paths.get(System.getProperty("user.dir") + imagePath);
        byte[] imageData = Files.readAllBytes(path);
        String mimeType = Files.probeContentType(path);

        return new ImageData(imageData, mimeType);
    }

    public static class ImageData {
        private final byte[] data;
        private final String mimeType;

        public ImageData(byte[] data, String mimeType) {
            this.data = data;
            this.mimeType = mimeType;
        }

        public byte[] getData() {
            return data;
        }

        public String getMimeType() {
            return mimeType;
        }
    }
}
