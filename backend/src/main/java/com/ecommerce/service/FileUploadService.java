package com.ecommerce.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class FileUploadService {

    @Value("${app.upload.path}")
    private String uploadPath;

    @Value("${app.upload.base-url}")
    private String baseUrl;

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    public String uploadFile(MultipartFile file, String folder) throws IOException {
        validateFile(file);
        
        // Create upload directory if it doesn't exist
        Path uploadDir = Paths.get(uploadPath, folder);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // Generate unique filename
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = getFileExtension(originalFilename);
        String filename = UUID.randomUUID().toString() + fileExtension;

        // Copy file to upload directory
        Path targetLocation = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        log.info("File uploaded successfully: {}", targetLocation);

        // Return the file URL
        return baseUrl + "/" + folder + "/" + filename;
    }

    public String uploadProductImage(MultipartFile file) throws IOException {
        return uploadFile(file, "products");
    }
    
    public String uploadStoreImage(MultipartFile file) throws IOException {
        return uploadFile(file, "stores");
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload empty file");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum limit of " + 
                    MAX_FILE_SIZE / (1024 * 1024) + "MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Only image files are allowed (JPEG, PNG, GIF, WebP)");
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex == -1 ? "" : filename.substring(lastDotIndex);
    }

    public boolean deleteFile(String fileUrl) {
        try {
            if (fileUrl == null || !fileUrl.startsWith(baseUrl)) {
                return false;
            }
            
            String relativePath = fileUrl.replace(baseUrl + "/", "");
            Path filePath = Paths.get(uploadPath, relativePath);
            
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("File deleted successfully: {}", filePath);
                return true;
            }
        } catch (IOException e) {
            log.error("Error deleting file: {}", fileUrl, e);
        }
        return false;
    }
}