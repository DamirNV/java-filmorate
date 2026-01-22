package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int idCounter = 1;

    @GetMapping
    public List<Film> findAll() {
        log.info("Получен запрос на получение всех фильмов. Текущее количество: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Получен запрос на создание фильма: {}", film);
        validate(film);
        film.setId(idCounter++);
        films.put(film.getId(), film);
        log.info("Фильм успешно создан с id: {}", film.getId());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        log.info("Получен запрос на обновление фильма с id: {}", film.getId());
        validate(film);
        films.put(film.getId(), film);
        log.info("Фильм с id {} успешно обновлен", film.getId());
        return film;
    }

    private void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            String error = "Название не может быть пустым";
            log.warn("Ошибка валидации фильма: {}", error);
            throw new ValidationException(error);
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            String error = "Описание не может быть длиннее 200 символов";
            log.warn("Ошибка валидации фильма: {}", error);
            throw new ValidationException(error);
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            String error = "Дата релиза не может быть раньше 28 декабря 1895 года";
            log.warn("Ошибка валидации фильма: {}", error);
            throw new ValidationException(error);
        }
        if (film.getDuration() <= 0) {
            String error = "Продолжительность должна быть положительной";
            log.warn("Ошибка валидации фильма: {}", error);
            throw new ValidationException(error);
        }
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(ValidationException e) {
        log.error("Ошибка валидации: {}", e.getMessage());
        return Map.of("error", e.getMessage());
    }
}