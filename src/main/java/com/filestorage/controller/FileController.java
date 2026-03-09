// controller/FileController.java
package com.filestorage.controller;

import com.filestorage.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@Tag(name = "Управление файлами", description = "API для работы с файлами и директориями")
@SecurityRequirement(name = "bearerAuth")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/list")
    @Operation(
            summary = "Получить список файлов",
            description = "Возвращает список файлов и директорий в указанном пути пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список файлов успешно получен",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = "[\"file1.txt\", \"folder1\", \"document.pdf\"]"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера"
            )
    })
    public ResponseEntity<List<String>> listFiles(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(
                    description = "Путь внутри хранилища пользователя (опционально)",
                    example = "docs/projects",
                    required = false
            )
            @RequestParam(defaultValue = "") String path) throws IOException {
        String username = authentication.getName();
        return ResponseEntity.ok(fileStorageService.listFiles(username, path));
    }

    @PostMapping("/upload")
    @Operation(
            summary = "Загрузить файл",
            description = "Загружает файл в хранилище пользователя. Максимальный размер файла определяется конфигурацией сервера."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Файл успешно загружен",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "\"File uploaded successfully\"")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректный запрос (отсутствует файл)"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера при сохранении файла"
            )
    })
    public ResponseEntity<String> uploadFile(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(
                    description = "Файл для загрузки",
                    required = true
            )
            @RequestParam("file") MultipartFile file,
            @Parameter(
                    description = "Путь для сохранения файла (опционально)",
                    example = "uploads/images",
                    required = false
            )
            @RequestParam(defaultValue = "") String path) throws IOException {
        String username = authentication.getName();
        fileStorageService.uploadFile(username, path, file);
        return ResponseEntity.ok("File uploaded successfully");
    }

    @GetMapping("/download")
    @Operation(
            summary = "Скачать файл",
            description = "Скачивает файл из хранилища пользователя. Возвращает файл в виде бинарных данных."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Файл успешно скачан",
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Файл не найден"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера при чтении файла"
            )
    })
    public ResponseEntity<Resource> downloadFile(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(
                    description = "Путь к файлу для скачивания",
                    example = "docs/report.pdf",
                    required = true
            )
            @RequestParam String path) throws IOException {
        String username = authentication.getName();
        Resource resource = fileStorageService.downloadFile(username, path);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PostMapping("/directory")
    @Operation(
            summary = "Создать директорию",
            description = "Создает новую директорию в хранилище пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Директория успешно создана",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "\"Directory created successfully\"")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректный путь или директория уже существует"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера при создании директории"
            )
    })
    public ResponseEntity<String> createDirectory(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(
                    description = "Путь новой директории",
                    example = "docs/new_folder",
                    required = true
            )
            @RequestParam String path) throws IOException {
        String username = authentication.getName();
        fileStorageService.createDirectory(username, path);
        return ResponseEntity.ok("Directory created successfully");
    }
}