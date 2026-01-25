# Filmorate - REST API для фильмов и пользователей

## 🎬 Описание проекта
**Filmorate** (от англ. film — «фильм» и rate — «оценивать») — это бэкенд-сервис для работы с фильмами и оценками пользователей. Сервис позволяет добавлять фильмы, пользователей, управлять ими и получать рекомендации по просмотру.

## 🚀 Функциональность

### Фильмы (`/films`)
- **GET /films** - получить список всех фильмов
- **POST /films** - создать новый фильм
- **PUT /films** - обновить существующий фильм

### Пользователи (`/users`)
- **GET /users** - получить список всех пользователей
- **POST /users** - создать нового пользователя
- **PUT /users** - обновить существующего пользователя

## 📋 Модели данных

### Фильм (Film)
```json
{
  "id": 1,
  "name": "Интерстеллар",
  "description": "Фантастический фильм о путешествиях в космосе",
  "releaseDate": "2014-10-26",
  "duration": 169
}
```

### Пользователь (User)
```json
{
  "id": 1,
  "email": "user@example.com",
  "login": "userlogin",
  "name": "Имя пользователя",
  "birthday": "1990-01-01"
}
```

## ✅ Валидация

### Для фильмов (реализована через кастомные и стандартные аннотации):
- ✅ **Название** не может быть пустым (`@NotBlank`)
- ✅ **Описание** ≤200 символов (`@Size(max=200)`)
- ✅ **Дата релиза** ≥28.12.1895 (`@ReleaseDate` - кастомная аннотация)
- ✅ **Продолжительность** >0 (`@Positive`)

### Для пользователей:
- ✅ **Email** не пустой и содержит @ (`@Email`)
- ✅ **Логин** не пустой и без пробелов (`@Pattern(regexp="\\S+")`)
- ✅ **Имя** может быть пустым → используется логин
- ✅ **Дата рождения** не в будущем (`@PastOrPresent`)

## 🎯 Особенности реализации

### Кастомная аннотация валидации `@ReleaseDate`
Проект включает собственную аннотацию для проверки даты релиза фильмов:

```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReleaseDateValidator.class)
public @interface ReleaseDate {
    String message() default "Дата релиза не может быть раньше 28 декабря 1895 года";
    String minDate() default "1895-12-28";
}
```

**Валидатор:**
```java
public class ReleaseDateValidator implements ConstraintValidator<ReleaseDate, LocalDate> {
    private LocalDate minDate;
    
    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext context) {
        return releaseDate == null || !releaseDate.isBefore(minDate);
    }
}
```

## 🛠 Технологии
- **Java 21**
- **Spring Boot 3.2.4**
- **Spring Web MVC**
- **Spring Validation** (включая кастомные аннотации)
- **Lombok**
- **SLF4J** для логирования
- **JUnit 5** для тестирования
- **MockMvc** для тестирования контроллеров

## 📁 Структура проекта
```
src/main/java/ru/yandex/practicum/filmorate/
├── FilmorateApplication.java          # Главный класс приложения
├── controller/
│   ├── FilmController.java           # Контроллер для фильмов
│   ├── UserController.java           # Контроллер для пользователей
│   ├── ValidationException.java      # Исключение для ошибок валидации
│   ├── NotFoundException.java        # Исключение для "не найдено"
│   └── GlobalExceptionHandler.java   # Глобальный обработчик исключений
├── model/
│   ├── Film.java                     # Модель фильма с аннотациями валидации
│   └── User.java                     # Модель пользователя с аннотациями валидации
└── validator/                        # Кастомные валидаторы
    ├── ReleaseDate.java              # Аннотация @ReleaseDate
    └── ReleaseDateValidator.java     # Валидатор для даты релиза

src/test/java/ru/yandex/practicum/filmorate/
├── controller/
│   ├── FilmControllerTest.java       # Тесты контроллера фильмов
│   └── UserControllerTest.java       # Тесты контроллера пользователей
└── FilmorateApplicationTests.java    # Тест запуска приложения
```

## 🧪 Тестирование
Проект включает:
1. **Unit-тесты** для контроллеров с использованием MockMvc
2. **Интеграционное тестирование** через Postman коллекцию
3. **Проверка граничных случаев** валидации (включая кастомную аннотацию)
4. **Покрытие тестами >95%**

### Запуск тестов:
```bash
mvn test
```

## 🚀 Запуск приложения

### 1. Клонировать репозиторий:
```bash
git clone https://github.com/ваш-username/java-filmorate.git
cd java-filmorate
```

### 2. Запустить приложение:
```bash
mvn spring-boot:run
```

Приложение будет доступно по адресу: `http://localhost:8080`

### 3. Проверить работоспособность:
```bash
# Получить все фильмы
curl http://localhost:8080/films

# Создать фильм (успешно)
curl -X POST http://localhost:8080/films \
  -H "Content-Type: application/json" \
  -d '{"name":"Фильм","description":"Описание","releaseDate":"2000-01-01","duration":120}'

# Создать фильм с неправильной датой (ошибка валидации через @ReleaseDate)
curl -X POST http://localhost:8080/films \
  -H "Content-Type: application/json" \
  -d '{"name":"Фильм","description":"Описание","releaseDate":"1890-01-01","duration":120}'
```

## 📝 Примеры запросов

### Создание пользователя:
```bash
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "login": "testuser",
    "birthday": "1990-01-01"
  }'
```

### Обновление фильма:
```bash
curl -X PUT http://localhost:8080/films \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "name": "Обновленное название",
    "description": "Новое описание",
    "releaseDate": "2000-01-01",
    "duration": 150
  }'
```

## 🔧 Обработка ошибок
- **400 Bad Request** - ошибки валидации данных (включая кастомные аннотации)
- **404 Not Found** - запрашиваемый ресурс не найден
- Все ошибки возвращаются в формате JSON с описанием

Пример ошибки валидации через `@ReleaseDate`:
```json
{
  "error": "releaseDate: Дата релиза не может быть раньше 28 декабря 1895 года"
}
```

## 📊 Ключевые особенности реализации
1. **Кастомная аннотация валидации** `@ReleaseDate` для проверки минимальной даты релиза
2. **Хранение данных в памяти** с использованием `HashMap<Integer, Film/User>`
3. **Автоматическая генерация ID** при создании новых сущностей
4. **Централизованная обработка исключений** через `GlobalExceptionHandler`
5. **Логирование всех операций** с использованием `@Slf4j`
6. **Полная поддержка REST API** с корректными HTTP статусами

## ✅ Требования
- Java 21 или выше
- Maven 3.6 или выше
- Git