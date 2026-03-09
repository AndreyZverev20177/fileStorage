// controller/AuthController.java
package com.filestorage.controller;


import com.filestorage.dto.JwtResponse;
import com.filestorage.dto.LoginRequest;
import com.filestorage.model.User;
import com.filestorage.service.UserService;
import com.filestorage.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Аутентификация", description = "API для регистрации и аутентификации пользователей")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @Operation(
            summary = "Аутентификация пользователя",
            description = "Аутентифицирует пользователя и возвращает JWT токен для доступа к защищенным API"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная аутентификация",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JwtResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\", \"type\": \"Bearer\", \"id\": 1, \"username\": \"user1\", \"roles\": null}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Неверные учетные данные",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\"error\": \"Invalid username or password\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\"error\": \"Login failed: Internal server error\"}")
                    )
            )
    })
    public ResponseEntity<?> login(
            @Parameter(
                    description = "Данные для входа",
                    required = true,
                    schema = @Schema(implementation = LoginRequest.class)
            )
            @RequestBody LoginRequest loginRequest) {
        try {
            logger.info("Login attempt for user: {}", loginRequest.getUsername());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtil.generateToken(authentication);

            User user = userService.findByUsername(loginRequest.getUsername());

            return ResponseEntity.ok(new JwtResponse(jwt, "Bearer", user.getId(), user.getUsername(), null));
        } catch (BadCredentialsException e) {
            logger.error("Bad credentials for user: {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));
        } catch (Exception e) {
            logger.error("Login error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Login failed: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Создает нового пользователя в системе. Требует уникальное имя пользователя и пароль длиной не менее 6 символов."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь успешно зарегистрирован",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = "{\"message\": \"User registered successfully\", \"username\": \"newuser\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные данные запроса",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(name = "Пустое имя пользователя", value = "{\"error\": \"Username cannot be empty\"}"),
                                    @ExampleObject(name = "Короткий пароль", value = "{\"error\": \"Password must be at least 6 characters long\"}"),
                                    @ExampleObject(name = "Пользователь уже существует", value = "{\"error\": \"Username already exists\"}")
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\"error\": \"Registration failed: Internal server error\"}")
                    )
            )
    })
    public ResponseEntity<?> register(
            @Parameter(
                    description = "Данные для регистрации",
                    required = true,
                    schema = @Schema(implementation = LoginRequest.class)
            )
            @RequestBody LoginRequest registerRequest) {
        try {
            logger.info("Registration attempt for user: {}", registerRequest.getUsername());

            if (registerRequest.getUsername() == null || registerRequest.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Username cannot be empty"));
            }

            if (registerRequest.getPassword() == null || registerRequest.getPassword().length() < 6) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Password must be at least 6 characters long"));
            }

            User user = userService.registerUser(registerRequest.getUsername(), registerRequest.getPassword());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("username", user.getUsername());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("Registration error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected registration error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Registration failed: " + e.getMessage()));
        }
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(Map.of("message", "Auth endpoint is working"));
    }
}