# Временное отключение CORS для отладки фронта

## Конфигурация CORS

Для отладки фронтенд-приложения CORS policy была временно отключена в конфигурации безопасности.

### Что было изменено

В файле [`SecurityConfig.java`](src/main/java/com/filestorage/config/SecurityConfig.java) добавлены:

1. **Включение CORS поддержки**:
   ```java
   .cors(cors -> cors.configurationSource(corsConfigurationSource()))
   ```

2. **Конфигурация CORS** (метод `corsConfigurationSource()`):
   - Разрешены все origins: `*`
   - Разрешены все HTTP методы: GET, POST, PUT, DELETE, OPTIONS, PATCH
   - Разрешены все заголовки
   - Разрешены credentials (куки, аутентификация)
   - Установлено максимальное время кэширования: 3600 секунд

### Для продакшена

**ВАЖНО**: Эта конфигурация предназначена только для отладки. Для продакшена необходимо:

1. Заменить `setAllowedOriginPatterns(List.of("*"))` на конкретные домены:
   ```java
   configuration.setAllowedOrigins(List.of(
       "https://ваш-фронтенд-домен.com",
       "http://localhost:3000" // для разработки
   ));
   ```

2. Ограничить разрешенные методы только необходимыми:
   ```java
   configuration.setAllowedMethods(List.of("GET", "POST", "DELETE"));
   ```

3. Указать конкретные заголовки:
   ```java
   configuration.setAllowedHeaders(List.of(
       "Authorization", 
       "Content-Type", 
       "Accept"
   ));
   ```

### Как временно отключить CORS (уже сделано)

Конфигурация уже применена. После перезапуска приложения фронтенд сможет обращаться к API с любого origin.

### Проверка работы

1. Запустите приложение:
   ```bash
   mvnw.cmd spring-boot:run
   ```

2. Фронтенд-приложение (например, на `http://localhost:3000`) теперь сможет:
   - Выполнять запросы к `http://localhost:8080/api/**`
   - Использовать все HTTP методы
   - Отправлять заголовки авторизации (JWT)
   - Работать с куками и сессиями

### Возврат к безопасной конфигурации

После завершения отладки закомментируйте или удалите строку:
```java
.cors(cors -> cors.configurationSource(corsConfigurationSource()))
```

Или обновите метод `corsConfigurationSource()` для использования безопасных настроек.

### Альтернативный способ - через application.properties

Можно также настроить CORS через properties файл (менее гибко):

```properties
# В application.properties
cors.allowed-origins=*
cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
cors.allowed-headers=*
cors.allow-credentials=true
```

Но текущая Java-конфигурация более гибкая и рекомендуется для Spring Security.