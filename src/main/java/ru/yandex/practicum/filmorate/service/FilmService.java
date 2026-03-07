package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    public void addLike(int filmId, int userId) {
        log.info("Добавление лайка: фильм {} от пользователя {}", filmId, userId);

        Film film = getFilmOrThrow(filmId);
        checkUserExists(userId);

        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        // TODO: реализовать через JdbcTemplate
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }

    public void removeLike(int filmId, int userId) {
        log.info("Удаление лайка: фильм {} от пользователя {}", filmId, userId);

        Film film = getFilmOrThrow(filmId);
        checkUserExists(userId);

        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        // TODO: реализовать через JdbcTemplate
        log.info("Пользователь {} удалил лайк у фильма {}", userId, filmId);
    }

    public List<Film> getPopular(int count) {
        log.info("Получение {} самых популярных фильмов", count);

        return filmStorage.getAll().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private Film getFilmOrThrow(int id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
    }

    private void checkUserExists(int id) {
        userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
    }

    public List<Film> getAllFilms() {
        log.debug("Запрос всех фильмов");
        return filmStorage.getAll();
    }

    public Film getFilmById(int id) {
        log.debug("Поиск фильма по id: {}", id);
        return filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
    }

    public Film createFilm(Film film) {
        log.info("Создание фильма: {}", film);
        return filmStorage.add(film);
    }

    public Film updateFilm(Film film) {
        log.info("Обновление фильма с id: {}", film.getId());
        return filmStorage.update(film);
    }

    public Film updateGenres(int filmId, Set<Genre> genres) {
        log.info("Обновление жанров для фильма {}", filmId);
        Film film = getFilmOrThrow(filmId);
        film.setGenres(genres);
        return filmStorage.update(film);
    }

    public Film updateMpa(int filmId, Mpa mpa) {
        log.info("Обновление рейтинга MPA для фильма {}", filmId);
        Film film = getFilmOrThrow(filmId);
        film.setMpa(mpa);
        return filmStorage.update(film);
    }
}