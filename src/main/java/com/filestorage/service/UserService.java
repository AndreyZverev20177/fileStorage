// service/UserService.java
package com.filestorage.service;

import com.filestorage.model.Role;
import com.filestorage.model.User;
import com.filestorage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private com.filestorage.service.FileStorageService fileStorageService;

    public User registerUser(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Set.of(Role.USER));

        // Создаем директорию для пользователя
        Path userPath = Paths.get(username);
        user.setStoragePath(userPath.toString());

        User savedUser = userRepository.save(user);
        fileStorageService.createUserDirectory(username);

        return savedUser;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}