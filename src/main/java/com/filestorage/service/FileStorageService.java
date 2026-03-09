// service/FileStorageService.java
package com.filestorage.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileStorageService {

    @Value("${file.storage.root-path}")
    private String rootPath;

    public void createUserDirectory(String username) {
        try {
            Path userPath = Paths.get(rootPath, username);
            Files.createDirectories(userPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create user directory", e);
        }
    }

    public List<String> listFiles(String username, String path) throws IOException {
        Path userPath = Paths.get(rootPath, username);
        Path fullPath = userPath.resolve(path).normalize();

//        if (!fullPath.startsWith(userPath)) {
//            throw new SecurityException("Access denied");
//        }

        return Files.list(fullPath)
                .map(p -> p.getFileName().toString() + (Files.isDirectory(p) ? "/" : ""))
                .collect(Collectors.toList());
    }

    public void uploadFile(String username, String path, MultipartFile file) throws IOException {
        Path userPath = Paths.get(rootPath, username);
        Path fullPath = userPath.resolve(path).normalize();

//        if (!fullPath.startsWith(userPath)) {
//            throw new SecurityException("Access denied");
//        }

        Files.createDirectories(fullPath);
        Path filePath = fullPath.resolve(file.getOriginalFilename());
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
    }

    public Resource downloadFile(String username, String path) throws MalformedURLException {
        Path userPath = Paths.get(rootPath, username);
        Path fullPath = userPath.resolve(path).normalize();

        if ( !Files.exists(fullPath) || Files.isDirectory(fullPath)) {
            throw new SecurityException("Access denied or file not found");
        }

        return new UrlResource(fullPath.toUri());
    }

    public void createDirectory(String username, String path) throws IOException {
        Path userPath = Paths.get(rootPath, username);
        Path fullPath = userPath.resolve(path).normalize();

//        if (!fullPath.startsWith(userPath)) {
//            throw new SecurityException("Access denied");
//        }

        Files.createDirectories(fullPath);
    }
}