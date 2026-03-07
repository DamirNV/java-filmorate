# Filmorate - REST API для фильмов и пользователей

## 🎬 Описание проекта
**Filmorate** — это бэкенд-сервис для работы с фильмами, оценками пользователей и социальными связями. 
Сервис позволяет добавлять фильмы и пользователей, ставить лайки, добавлять в друзья (односторонняя дружба) 
и получать рекомендации на основе популярности. Данные хранятся в базе данных H2.

---

## 🚀 Функциональность

### 🎥 Фильмы (`/films`)

| Метод | Эндпоинт | Описание |
|-------|----------|----------|
| `GET` | `/films` | Получить все фильмы |
| `GET` | `/films/{id}` | Получить фильм по ID |
| `POST` | `/films` | Создать новый фильм |
| `PUT` | `/films` | Обновить фильм |
| `PUT` | `/films/{id}/like/{userId}` | Поставить лайк |
| `DELETE` | `/films/{id}/like/{userId}` | Убрать лайк |
| `GET` | `/films/popular?count={count}` | Топ N популярных фильмов |

### 👥 Пользователи (`/users`)

| Метод | Эндпоинт | Описание |
|-------|----------|----------|
| `GET` | `/users` | Получить всех пользователей |
| `GET` | `/users/{id}` | Получить пользователя по ID |
| `POST` | `/users` | Создать нового пользователя |
| `PUT` | `/users` | Обновить пользователя |
| `PUT` | `/users/{id}/friends/{friendId}` | Добавить в друзья (односторонняя дружба) |
| `DELETE` | `/users/{id}/friends/{friendId}` | Удалить из друзей |
| `GET` | `/users/{id}/friends` | Получить список друзей |
| `GET` | `/users/{id}/friends/common/{otherId}` | Получить общих друзей |

### 🎭 Жанры (`/genres`)

| Метод | Эндпоинт | Описание |
|-------|----------|----------|
| `GET` | `/genres` | Получить все жанры |
| `GET` | `/genres/{id}` | Получить жанр по ID |

### ⭐ Рейтинги MPA (`/mpa`)

| Метод | Эндпоинт | Описание |
|-------|----------|----------|
| `GET` | `/mpa` | Получить все рейтинги |
| `GET` | `/mpa/{id}` | Получить рейтинг по ID |

---

## 📋 Модели данных

### 🎬 Фильм (Film)
```json
{
  "id": 1,
  "name": "Интерстеллар",
  "description": "Фантастический фильм о путешествиях в космосе",
  "releaseDate": "2014-10-26",
  "duration": 169,
  "mpa": { "id": 3, "name": "PG-13" },
  "genres": [
    { "id": 1, "name": "Комедия" },
    { "id": 2, "name": "Драма" }
  ],
  "likes": [1, 2, 3]
}
```

### 👤 Пользователь (User)
```json
{
  "id": 1,
  "email": "user@example.com",
  "login": "userlogin",
  "name": "Имя пользователя",
  "birthday": "1990-01-01"
}
```

### 🎭 Жанр (Genre)
```json
{
  "id": 1,
  "name": "Комедия"
}
```

### ⭐ Рейтинг MPA (Mpa)
```json
{
  "id": 1,
  "name": "G"
}
```

---

## ✅ Валидация

Валидация происходит на уровне DTO (объектов запроса):

### 🎬 Для фильмов (`CreateFilmRequest`):
| Поле | Аннотация | Правило |
|------|-----------|---------|
| `name` | `@NotBlank` | Не может быть пустым |
| `description` | `@Size(max=200)` | ≤200 символов |
| `releaseDate` | `@NotNull` + `@ReleaseDate` | Обязательна, ≥28.12.1895 |
| `duration` | `@Positive` | >0 |
| `mpa` | `@NotNull` | Обязателен |

### 👤 Для пользователей (`CreateUserRequest`):
| Поле | Аннотация | Правило |
|------|-----------|---------|
| `email` | `@Email` + `@NotBlank` | Валидный email, не пустой |
| `login` | `@NotBlank` + `@Pattern(regexp="\\S+")` | Не пустой, без пробелов |
| `name` | - | Если не указано → используется login |
| `birthday` | `@PastOrPresent` | Не в будущем |

---

## 🎯 Особенности реализации

### 🏗 Архитектура
- **Слоистая архитектура**: Controller → Service → Repository
- **Dependency Injection** через конструкторы (`@RequiredArgsConstructor`)
- **Репозитории** с использованием `JdbcTemplate`
- **Бизнес-логика** вынесена в Service-слой
- **Мапперы** для преобразования Model ↔ DTO

### 💾 Хранение данных
- **H2 Database** (файловая для production, in-memory для тестов)
- **Schema.sql** для инициализации структуры БД
- **Репозитории** с `Optional` для безопасной работы с данными
- **Внешние ключи** для целостности данных

### 🎨 Кастомная валидация
```java
@ReleaseDate(minDate = "1895-12-28")
private LocalDate releaseDate;
```

### 🌐 RESTful API
- Полное соответствие REST-стандартам
- Корректные HTTP-методы (GET, POST, PUT, DELETE)
- PathVariable и RequestParam для гибкости

### 🎯 Обработка ошибок
- `@RestControllerAdvice` для централизованной обработки
- **400** — ошибки валидации
- **404** — ресурс не найден (`NotFoundException`)
- **500** — внутренние ошибки сервера

---

## 🛠 Технологии

| Компонент | Технология |
|-----------|------------|
| **Язык** | Java 21 |
| **Фреймворк** | Spring Boot 3.2.4 |
| **База данных** | H2 Database |
| **Доступ к данным** | Spring JDBC + JdbcTemplate |
| **Web** | Spring Web MVC |
| **Валидация** | Spring Validation + Custom Annotations |
| **Логирование** | SLF4J + Logbook 3.7.2 |
| **Утилиты** | Lombok |
| **Тестирование** | JUnit 5, MockMvc, @JdbcTest |
| **Сборка** | Maven |

---

## ⚙️ Конфигурация

### application.properties
```properties
spring.sql.init.mode=always
spring.datasource.url=jdbc:h2:file:./db/filmorate
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
logging.level.org.zalando.logbook=TRACE
```

### application-test.properties (для тестов)
```properties
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.sql.init.mode=always
```

---

## 📁 Структура проекта

```
src/main/java/ru/yandex/practicum/filmorate/
├── FilmorateApplication.java
├── controller/
│   ├── FilmController.java
│   ├── UserController.java
│   ├── GenreController.java
│   ├── MpaController.java
│   ├── GlobalExceptionHandler.java
│   └── NotFoundException.java
├── service/
│   ├── FilmService.java
│   ├── UserService.java
│   ├── GenreService.java
│   └── MpaService.java
├── dal/
│   ├── BaseRepository.java
│   ├── mappers/
│   │   ├── FilmRowMapper.java
│   │   └── UserRowMapper.java
│   └── repositories/
│       ├── FilmRepository.java
│       ├── UserRepository.java
│       ├── GenreRepository.java
│       └── MpaRepository.java
├── dto/
│   ├── film/
│   │   ├── CreateFilmRequest.java
│   │   ├── UpdateFilmRequest.java
│   │   └── FilmResponse.java
│   └── user/
│       ├── CreateUserRequest.java
│       ├── UpdateUserRequest.java
│       └── UserResponse.java
├── mapper/
│   ├── FilmMapper.java
│   └── UserMapper.java
├── model/
│   ├── Film.java
│   ├── User.java
│   ├── Genre.java
│   ├── Mpa.java
│   ├── Friendship.java
│   └── FriendshipStatus.java
├── exception/
│   └── DuplicatedDataException.java
└── validator/
    ├── ReleaseDate.java
    └── ReleaseDateValidator.java

src/main/resources/
├── application.properties
└── schema.sql

src/test/java/ru/yandex/practicum/filmorate/
├── controller/
│   ├── FilmControllerTest.java
│   ├── UserControllerTest.java
│   ├── GenreControllerTest.java
│   └── MpaControllerTest.java
├── service/
│   ├── FilmServiceTest.java
│   ├── UserServiceTest.java
│   ├── GenreServiceTest.java
│   └── MpaServiceTest.java
├── dal/
│   └── repositories/
│       ├── FilmRepositoryTest.java
│       ├── UserRepositoryTest.java
│       ├── GenreRepositoryTest.java
│       └── MpaRepositoryTest.java
├── dto/
│   ├── CreateFilmRequestTest.java
│   ├── UpdateFilmRequestTest.java
│   ├── CreateUserRequestTest.java
│   └── UpdateUserRequestTest.java
├── mapper/
│   ├── FilmMapperTest.java
│   └── UserMapperTest.java
├── model/
│   ├── FilmTest.java
│   ├── UserTest.java
│   ├── GenreTest.java
│   └── MpaTest.java
└── FilmorateApplicationTests.java

src/test/resources/
└── application-test.properties
```

---

## 🗄️ Схема базы данных

Файл со схемой: [`schema.sql`](src/main/resources/schema.sql) — содержит все SQL-скрипты для создания таблиц.

### Основные таблицы:
| Таблица | Назначение |
|---------|------------|
| `users` | Хранение пользователей |
| `films` | Хранение фильмов |
| `genres` | Справочник жанров |
| `mpa_rating` | Справочник рейтингов MPA |
| `film_genres` | Связь фильмов с жанрами |
| `likes` | Лайки пользователей к фильмам |
| `friendship_status` | Статусы дружбы |
| `friendship` | Односторонние связи дружбы |

### SQL-скрипты:
```sql
-- Таблица пользователей
CREATE TABLE IF NOT EXISTS users (
    user_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(100),
    birthday DATE NOT NULL
);

-- Таблица рейтингов MPA
CREATE TABLE IF NOT EXISTS mpa_rating (
    mpa_rating_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    description VARCHAR(200)
);

-- Таблица фильмов
CREATE TABLE IF NOT EXISTS films (
    film_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    release_date DATE NOT NULL,
    duration INTEGER NOT NULL,
    mpa_rating_id INTEGER NOT NULL REFERENCES mpa_rating(mpa_rating_id)
);

-- Таблица жанров
CREATE TABLE IF NOT EXISTS genres (
    genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Связь фильмов с жанрами
CREATE TABLE IF NOT EXISTS film_genres (
    film_id INTEGER NOT NULL REFERENCES films(film_id) ON DELETE CASCADE,
    genre_id INTEGER NOT NULL REFERENCES genres(genre_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, genre_id)
);

-- Таблица лайков
CREATE TABLE IF NOT EXISTS likes (
    film_id INTEGER NOT NULL REFERENCES films(film_id) ON DELETE CASCADE,
    user_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, user_id)
);

-- Таблица статусов дружбы
CREATE TABLE IF NOT EXISTS friendship_status (
    status_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
);

-- Таблица дружбы (односторонняя)
CREATE TABLE IF NOT EXISTS friendship (
    user_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    friend_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    status_id INTEGER NOT NULL REFERENCES friendship_status(status_id),
    PRIMARY KEY (user_id, friend_id),
    CHECK (user_id != friend_id)
);
```

---

## 📊 Примеры SQL-запросов

### Получить всех пользователей
```sql
SELECT * FROM users;
```

### Получить все фильмы с рейтингом MPA
```sql
SELECT f.*, m.code as mpa_name 
FROM films f
LEFT JOIN mpa_rating m ON f.mpa_rating_id = m.mpa_rating_id;
```

### Получить топ-10 популярных фильмов
```sql
SELECT f.film_id, f.title, COUNT(l.user_id) as likes_count
FROM films f
LEFT JOIN likes l ON f.film_id = l.film_id
GROUP BY f.film_id
ORDER BY likes_count DESC
LIMIT 10;
```

### Получить друзей пользователя (односторонняя дружба)
```sql
SELECT u.* 
FROM users u
JOIN friendship f ON u.user_id = f.friend_id
WHERE f.user_id = 1 AND f.status_id = 1;
```

### Получить общих друзей
```sql
SELECT u.*
FROM users u
JOIN friendship f1 ON u.user_id = f1.friend_id AND f1.user_id = 1 AND f1.status_id = 1
JOIN friendship f2 ON u.user_id = f2.friend_id AND f2.user_id = 2 AND f2.status_id = 1;
```

### Получить жанры фильма
```sql
SELECT g.*
FROM genres g
JOIN film_genres fg ON g.genre_id = fg.genre_id
WHERE fg.film_id = 1
ORDER BY g.genre_id;
```

---

## 🧪 Тестирование

### ✅ Unit-тесты
- **Контроллеры** — тестирование эндпоинтов с моками сервисов
- **Сервисы** — тестирование бизнес-логики с моками репозиториев
- **Репозитории** — интеграционные тесты с H2 (`@JdbcTest`)
- **DTO** — тестирование валидации
- **Мапперы** — тестирование преобразований
- **Модели** — тестирование equals/hashCode

### 📊 Покрытие кода
| Метрика | Результат |
|---------|-----------|
| **Классы** | 100% (25/25) |
| **Методы** | 100% (118/118) |
| **Строки** | 98% (395/399) |
| **Ветвления** | 85% (108/126) |

### 📬 Интеграционное тестирование
- Postman-коллекция для проверки всех эндпоинтов (144 теста)
- Проверка HTTP-статусов и форматов ответов

### 📥 Postman-коллекция
Файл коллекции: [`postman.json`](postman.json) — импортируйте в Postman для полного тестирования API.

### Запуск тестов
```bash
mvn clean test
```

---

## 📊 Логирование

### Logbook (HTTP-логи)
Автоматическое логирование всех запросов/ответов в формате JSON:
```json
{
  "origin": "remote",
  "type": "request",
  "method": "POST",
  "uri": "http://localhost:8080/users",
  "body": {"login": "user1", "email": "user@test.com"}
}
```

### Собственные логи
- `INFO` — основные операции (создание, обновление, добавление в друзья, лайки)
- `DEBUG` — внутренние операции (получение по ID, получение всех)

---

## 🚀 Быстрый старт

### 1. Клонировать репозиторий
```bash
git clone https://github.com/ваш-username/filmorate.git
cd filmorate
```

### 2. Собрать проект
```bash
mvn clean package
```

### 3. Запустить приложение
```bash
mvn spring-boot:run
```

### 4. Примеры запросов
```bash
# Создать пользователя
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"email":"user@test.com","login":"user1","birthday":"1990-01-01"}'

# Создать фильм
curl -X POST http://localhost:8080/films \
  -H "Content-Type: application/json" \
  -d '{"name":"Inception","description":"Movie","releaseDate":"2010-07-16","duration":148,"mpa":{"id":3}}'

# Поставить лайк
curl -X PUT http://localhost:8080/films/1/like/1

# Добавить в друзья
curl -X PUT http://localhost:8080/users/1/friends/2

# Получить топ фильмов
curl http://localhost:8080/films/popular?count=5

# Открыть H2 консоль
# http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:file:./db/filmorate
# User: sa
# Password: password
```

---

## ✅ Требования к системе
- **Java 21** или выше
- **Maven 3.8+**
- **Git**
- **Postman** (для ручного тестирования)

---

**Разработано с ❤️ для настоящих киноманов** 🎬🍿
```