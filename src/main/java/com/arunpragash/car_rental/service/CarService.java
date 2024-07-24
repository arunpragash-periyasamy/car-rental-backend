package com.arunpragash.car_rental.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.arunpragash.car_rental.model.requestModel.CarRequest;
import com.arunpragash.car_rental.model.requestModel.CarResponse;
import com.arunpragash.car_rental.model.table.Address;
import com.arunpragash.car_rental.model.table.Car;
import com.arunpragash.car_rental.model.table.CarImages;
import com.arunpragash.car_rental.model.table.CarModel;
import com.arunpragash.car_rental.model.table.CarPrice;
import com.arunpragash.car_rental.model.table.CarSpecs;
import com.arunpragash.car_rental.model.table.User;
import com.arunpragash.car_rental.repository.AddressRepository;
import com.arunpragash.car_rental.repository.CarImagesRepository;
import com.arunpragash.car_rental.repository.CarModelRepository;
import com.arunpragash.car_rental.repository.CarPriceRepository;
import com.arunpragash.car_rental.repository.CarRepository;
import com.arunpragash.car_rental.repository.CarSpecsRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Autowired
    private AddressRepository addressRepository;

    @Value("${app.url}")
    private String appURL;
    
    @Transactional
    public void saveCar(CarRequest carRequest, List<MultipartFile> images, String userName) throws IOException {
        // Get the user
        User user = userService.getUser(userName);

        CarModel carModel;
        Car car;
        Address address;
        CarSpecs carSpecs;
        CarPrice carPrice;

        if (carRequest.getId() == null || carRequest.getId() == 0) {
            // Create new CarModel
            carModel = new CarModel();
            carModel.setBrandName(carRequest.getBrandName());
            carModel.setModelName(carRequest.getModelName());
            carModel.setBody(carRequest.getBody());
            carModel.setSeats(carRequest.getSeats());
            carModel = carModelRepository.save(carModel);

            // Create new Car
            car = new Car();
            car.setName(carRequest.getCarName());
            car.setVin(carRequest.getVin());
            car.setYear(carRequest.getYear());
            car.setModel(carModel);
            car.setUser(user);
            car = carRepository.save(car);

            // Create new Address
            address = new Address();
            address.setUser(user);
            address.setCar(car);
            address.setCity(carRequest.getCity());
            address.setState(carRequest.getState());
            address.setCountry(carRequest.getCountry());
            address.setAddress(carRequest.getAddress());
            address.setPinCode(carRequest.getPinCode());
            address = addressRepository.save(address);

            // Create new CarSpecs
            carSpecs = new CarSpecs();
            carSpecs.setCar(car);
            carSpecs.setGearType(carRequest.getGearType());
            carSpecs.setMileage(carRequest.getMileage());
            carSpecs.setFuelType(carRequest.getFuelType());
            carSpecs.setDrivetrain(carRequest.getDrivetrain());
            carSpecs.setEnginePower(carRequest.getEnginePower());
            carSpecs.setBrake(carRequest.getBrake());
            carSpecsRepository.save(carSpecs);

            // Create new CarPrice
            carPrice = new CarPrice();
            carPrice.setCar(car);
            carPrice.setAmount(carRequest.getAmount());
            carPrice.setDoorDeliveryAndPickup(carRequest.getDoorDeliveryPrice());
            carPrice.setTripProtectionFees(carRequest.getTripProtectionFee());
            carPrice.setTax(carRequest.getTax());
            carPrice.setConvenienceFee(carRequest.getConvienenceFee());
            carPrice.setRefundableDeposit(carRequest.getRefundableDeposit());
            carPriceRepository.save(carPrice);
        } else {
            // Update existing Car
            car = carRepository.findById(carRequest.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Car not found with id: " + carRequest.getId()));

            // Update CarModel
            carModel = car.getModel();
            carModel.setBrandName(carRequest.getBrandName());
            carModel.setModelName(carRequest.getModelName());
            carModel.setBody(carRequest.getBody());
            carModel.setSeats(carRequest.getSeats());
            carModel = carModelRepository.save(carModel);

            // Update Car
            car.setName(carRequest.getCarName());
            car.setVin(carRequest.getVin());
            car.setYear(carRequest.getYear());
            car.setModel(carModel);
            car.setUser(user);
            car = carRepository.save(car);

            // Update Address
            address = addressRepository.findByCarId(car.getId());
            address.setUser(user);
            address.setCar(car);
            address.setCity(carRequest.getCity());
            address.setState(carRequest.getState());
            address.setCountry(carRequest.getCountry());
            address.setAddress(carRequest.getAddress());
            address.setPinCode(carRequest.getPinCode());
            address = addressRepository.save(address);

            // Update CarSpecs
            carSpecs = carSpecsRepository.findByCarId(car.getId());
            carSpecs.setGearType(carRequest.getGearType());
            carSpecs.setMileage(carRequest.getMileage());
            carSpecs.setFuelType(carRequest.getFuelType());
            carSpecs.setDrivetrain(carRequest.getDrivetrain());
            carSpecs.setEnginePower(carRequest.getEnginePower());
            carSpecs.setBrake(carRequest.getBrake());
            carSpecsRepository.save(carSpecs);

            // Update CarPrice
            carPrice = carPriceRepository.findByCarId(car.getId());
            carPrice.setDoorDeliveryAndPickup(carRequest.getDoorDeliveryPrice());
            carPrice.setTripProtectionFees(carRequest.getTripProtectionFee());
            carPrice.setTax(carRequest.getTax());
            carPrice.setConvenienceFee(carRequest.getConvienenceFee());
            carPrice.setRefundableDeposit(carRequest.getRefundableDeposit());
            carPriceRepository.save(carPrice);
            carImagesRepository.deleteAllByCarId(car.getId());
        }
        // Save Images
        saveImages(car, images);
    }


    @Transactional
    @SuppressWarnings("null")
    private void saveImages(Car car, List<MultipartFile> images) throws IOException {
        if (car == null || images == null || images.isEmpty()) {
            throw new IllegalArgumentException("Car or images list is null or empty");
        }

        String carId = car.getId().toString();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(dtf);

        for (MultipartFile file : images) {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("Uploaded file is null or empty");
            }

            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = getFileExtension(originalFileName, file.getContentType());
            String uuid = UUID.randomUUID().toString();
            String generatedFileName = carId + "_" + timestamp + "_" + uuid + fileExtension;

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
            carImages.setPath(UPLOAD_DIR + generatedFileName);
            carImagesRepository.save(carImages);
        }
    }

    private String getFileExtension(String fileName, String contentType) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf("."));
        } else if (contentType != null && contentType.startsWith("image/")) {
            return "." + contentType.substring(6);
        } else {
            return ".jpg"; // Default extension
        }
    }



    public CarResponse getCar(Long id) {
        Optional<Car> carOptional = carRepository.findById(id);
        if (carOptional.isPresent()) {
            Car car = carOptional.get();
            CarModel carModel = car.getModel();
            CarSpecs carSpecs = carSpecsRepository.findByCarId(car.getId());
            CarPrice carPrice = carPriceRepository.findByCarId(car.getId());
            Address address = addressRepository.findByCarId(car.getId());
            List<String> images = carImagesRepository.findByCarId(car.getId())
                    .stream().map(carImage -> appURL + "/api/cars/images/"+ carImage.getId())
                    .collect(Collectors.toList());
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
                    carPrice.getConvenienceFee(),
                    carPrice.getRefundableDeposit(),
                    address.getAddress(),
                    address.getCity(),
                    address.getState(),
                    address.getPinCode(),
                    address.getCountry(),
                    images);
        }
        return null;
    }
  
    public List<CarResponse> getAllCarsByUser(String userName) {
        User user = userService.getUser(userName);
        return carRepository.findAllByUser(user).stream().map(car -> {
            CarModel carModel = car.getModel();
            CarSpecs carSpecs = carSpecsRepository.findByCarId(car.getId());
            CarPrice carPrice = carPriceRepository.findByCarId(car.getId());
            Address address = addressRepository.findByCarId(car.getId());
            List<String> images = carImagesRepository.findByCarId(car.getId())
                    .stream().map(carImage -> appURL + "/api/cars/images/"+ carImage.getId())
                    .collect(Collectors.toList());

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
                    carPrice.getConvenienceFee(),
                    carPrice.getRefundableDeposit(),
                    address.getAddress(),
                    address.getCity(),
                    address.getState(),
                    address.getPinCode(),
                    address.getCountry(),
                    images);
        }).collect(Collectors.toList());
    }
    
    public List<CarResponse> getAllCars() {
        return carRepository.findAll().stream().map(car -> {
            CarModel carModel = car.getModel();
            CarSpecs carSpecs = carSpecsRepository.findByCarId(car.getId());
            CarPrice carPrice = carPriceRepository.findByCarId(car.getId());
            Address address = addressRepository.findByCarId(car.getId());
            List<String> images = carImagesRepository.findByCarId(car.getId())
                    .stream().map(carImage -> appURL + "/api/cars/images/"+ carImage.getId())
                    .collect(Collectors.toList());

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
                    carPrice.getConvenienceFee(),
                    carPrice.getRefundableDeposit(),
                    address.getAddress(),
                    address.getCity(),
                    address.getState(),
                    address.getPinCode(),
                    address.getCountry(),
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

















// public void saveCar(CarRequest carRequest, List<MultipartFile> images, String
// userName) throws IOException {
// // Save CarModel
// User user = userService.getUser(userName);
// CarModel carModel = new CarModel();
// carModel.setBrandName(carRequest.getBrandName());
// carModel.setModelName(carRequest.getModelName());
// carModel.setBody(carRequest.getBody());
// carModel.setSeats(carRequest.getSeats());
// carModel = carModelRepository.save(carModel);

// // Save Car
// Car car = new Car();
// car.setName(carRequest.getCarName());
// car.setVin(carRequest.getVin());
// car.setYear(carRequest.getYear());
// car.setModel(carModel);
// car.setUser(user);
// car = carRepository.save(car);

// // save address
// Address address = new Address();
// address.setUser(user);
// address.setCar(car);
// address.setCity(carRequest.getCity());
// address.setCountry(carRequest.getCountry());
// address.setAddress(carRequest.getAddress());
// address.setPinCode(carRequest.getPinCode());
// address = addressRepository.save(address);
// // Save CarSpecs
// CarSpecs carSpecs = new CarSpecs();
// carSpecs.setCar(car);
// carSpecs.setGearType(carRequest.getGearType());
// carSpecs.setMileage(carRequest.getMileage());
// carSpecs.setFuelType(carRequest.getFuelType());
// carSpecs.setDrivetrain(carRequest.getDrivetrain());
// carSpecs.setEnginePower(carRequest.getEnginePower());
// carSpecs.setBrake(carRequest.getBrake());
// carSpecsRepository.save(carSpecs);

// // Save CarPrice
// CarPrice carPrice = new CarPrice();
// carPrice.setCar(car);
// carPrice.setAmount(carRequest.getAmount());
// carPrice.setDoorDeliveryAndPickup(carRequest.getDoorDeliveryPrice());
// carPrice.setTripProtectionFees(carRequest.getTripProtectionFee());
// carPrice.setTax(carRequest.getTax());
// carPrice.setConvenienceFee(carRequest.getConvenienceFee());
// carPrice.setRefundableDeposit(carRequest.getRefundableDeposit());
// carPriceRepository.save(carPrice);

// // Save Images
// saveImages(car, images);
// }