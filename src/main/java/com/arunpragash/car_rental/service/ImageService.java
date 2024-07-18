package com.arunpragash.car_rental.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.arunpragash.car_rental.model.Image;
import com.arunpragash.car_rental.repository.ImageRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageService {

    private final String UPLOAD_DIR = "uploads/";

    @Autowired
    private ImageRepository imageRepository;

    public Image saveImage(MultipartFile file) throws IOException {
        // Create the directory if it does not exist
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Save the file locally
        String fileName = file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR, fileName);
        Files.write(filePath, file.getBytes());

        // Save the image metadata in the database
        Image image = new Image();
        image.setName(fileName);
        image.setPath(filePath.toString());
        return imageRepository.save(image);
    }
}
