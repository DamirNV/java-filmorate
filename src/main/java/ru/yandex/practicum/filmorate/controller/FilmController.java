package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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
    public Film create(@Valid @RequestBody Film film) {
        log.info("Получен запрос на создание фильма: {}", film);
        film.setId(idCounter++);
        films.put(film.getId(), film);
        log.info("Фильм успешно создан с id: {}", film.getId());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Получен запрос на обновление фильма с id: {}", film.getId());

        if (film.getId() == 0 || !films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден");
        }

        films.put(film.getId(), film);
        log.info("Фильм с id {} успешно обновлен", film.getId());
        return film;
    }

}