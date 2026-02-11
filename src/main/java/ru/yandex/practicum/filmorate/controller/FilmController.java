package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @GetMapping
    public List<Film> findAll() {
        log.info("Получен запрос на получение всех фильмов");
        return filmStorage.getAll();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        log.info("GET /films/{}", id);
        return filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
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

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        log.info("PUT /films/{}/like/{}", id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        log.info("DELETE /films/{}/like/{}", id, userId);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") int count) {
        log.info("GET /films/popular?count={}", count);
        return filmService.getPopular(count);
    }
}