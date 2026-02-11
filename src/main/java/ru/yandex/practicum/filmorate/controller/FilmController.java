package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmStorage filmStorage;

    @GetMapping
    public List<Film> findAll() {
        log.info("Получен запрос на получение всех фильмов");
        return filmStorage.getAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Получен запрос на создание фильма: {}", film);
        Film createdFilm = filmStorage.add(film);
        log.info("Фильм успешно создан с id: {}", createdFilm.getId());
        return createdFilm;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Получен запрос на обновление фильма с id: {}", film.getId());
        Film updatedFilm = filmStorage.update(film);
        log.info("Фильм с id {} успешно обновлен", updatedFilm.getId());
        return updatedFilm;
    }
}