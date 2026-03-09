# Документация API File Storage

## Swagger/OpenAPI Документация

Проект интегрирован с SpringDoc OpenAPI для автоматической генерации документации API.

### Доступные URL

1. **Swagger UI**: http://localhost:8080/swagger-ui.html
2. **OpenAPI JSON спецификация**: http://localhost:8080/v3/api-docs
3. **OpenAPI YAML спецификация**: http://localhost:8080/v3/api-docs.yaml

### Основные возможности Swagger UI

1. **Интерактивная документация**: Полный список всех API endpoints с описанием
2. **Тестирование API**: Возможность отправлять запросы прямо из браузера
3. **Авторизация**: Поддержка JWT аутентификации
4. **Схемы данных**: Автоматическая генерация моделей данных

### Аутентификация в Swagger

1. Перейдите на страницу Swagger UI
2. Нажмите кнопку "Authorize" в правом верхнем углу
3. Введите JWT токен в формате: `Bearer <ваш_токен>`
4. Токен можно получить через `/api/auth/login`

### Основные API Endpoints

#### Аутентификация (`/api/auth`)
- `POST /api/auth/login` - Вход в систему
- `POST /api/auth/register` - Регистрация нового пользователя
- `GET /api/auth/test` - Тестовый endpoint

#### Управление файлами (`/api/files`)
- `GET /api/files/list` - Получить список файлов
- `POST /api/files/upload` - Загрузить файл
- `GET /api/files/download` - Скачать файл
- `POST /api/files/directory` - Создать директорию

### Примеры использования

#### 1. Регистрация пользователя
```bash
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password123"}'
```

#### 2. Аутентификация
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password123"}'
```

#### 3. Получение списка файлов (с JWT токеном)
```bash
curl -X GET "http://localhost:8080/api/files/list" \
  -H "Authorization: Bearer <ваш_jwt_токен>"
```

### Конфигурация Swagger

Конфигурация находится в классе [`OpenApiConfig.java`](src/main/java/com/filestorage/config/OpenApiConfig.java):
- Название API: File Storage API
- Версия: 1.0.0
- Схема безопасности: JWT Bearer Token
- Сервер: http://localhost:8080

### Аннотации OpenAPI в коде

Проект использует следующие аннотации для документирования API:

1. **@Tag** - Группировка endpoints по категориям
2. **@Operation** - Описание операции (summary, description)
3. **@ApiResponses** - Описание возможных ответов
4. **@Parameter** - Описание параметров запроса
5. **@SecurityRequirement** - Требования безопасности
6. **@Schema** - Описание моделей данных

### Тестирование

Для тестирования документации:

1. Запустите приложение:
   ```bash
   ./mvnw spring-boot:run
   ```

2. Откройте браузер и перейдите по адресу:
   ```
   http://localhost:8080/swagger-ui.html
   ```

3. Протестируйте endpoints через Swagger UI

### Примечания

- Swagger UI доступен без аутентификации
- Для тестирования защищенных endpoints требуется JWT токен
- Все изменения в контроллерах автоматически отражаются в документации
- Документация генерируется во время выполнения приложения