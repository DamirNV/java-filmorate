package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.CreateFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.FilmResponse;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public List<FilmResponse> findAll() {
        log.info("Получен запрос на получение всех фильмов");
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public FilmResponse getFilmById(@PathVariable int id) {
        log.info("GET /films/{}", id);
        return filmService.getFilmById(id);
    }

    @PostMapping
    public FilmResponse create(@Valid @RequestBody CreateFilmRequest request) {
        log.info("Получен запрос на создание фильма: {}", request);
        return filmService.createFilm(request);
    }

    @PutMapping
    public FilmResponse update(@Valid @RequestBody UpdateFilmRequest request) {
        log.info("Получен запрос на обновление фильма с id: {}", request.getId());
        return filmService.updateFilm(request);
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
    public List<FilmResponse> getPopular(@RequestParam(defaultValue = "10") int count) {
        log.info("GET /films/popular?count={}", count);
        return filmService.getPopular(count);
    }

    @PutMapping("/{id}/genres")
    public FilmResponse updateGenres(@PathVariable int id, @RequestBody Set<Genre> genres) {
        log.info("PUT /films/{}/genres с {} жанрами", id, genres.size());
        return filmService.updateGenres(id, genres);
    }

    @PutMapping("/{id}/mpa")
    public FilmResponse updateMpa(@PathVariable int id, @RequestBody Mpa mpa) {
        log.info("PUT /films/{}/mpa с рейтингом {}", id, mpa.getName());
        return filmService.updateMpa(id, mpa);
    }
}