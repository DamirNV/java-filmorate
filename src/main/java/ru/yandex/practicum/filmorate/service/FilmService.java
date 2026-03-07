package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.NotFoundException;
import ru.yandex.practicum.filmorate.dal.repositories.FilmRepository;
import ru.yandex.practicum.filmorate.dal.repositories.GenreRepository;
import ru.yandex.practicum.filmorate.dal.repositories.MpaRepository;
import ru.yandex.practicum.filmorate.dal.repositories.UserRepository;
import ru.yandex.practicum.filmorate.dto.film.CreateFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.FilmResponse;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final MpaRepository mpaRepository;

    public List<FilmResponse> getAllFilms() {
        log.debug("Запрос всех фильмов");
        return filmRepository.findAll().stream()
                .map(FilmMapper::mapToFilmResponse)
                .collect(Collectors.toList());
    }

    public FilmResponse getFilmById(int id) {
        log.debug("Поиск фильма по id: {}", id);
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
        return FilmMapper.mapToFilmResponse(film);
    }

    public FilmResponse createFilm(CreateFilmRequest request) {
        log.info("Создание фильма: {}", request);

        validateMpaAndGenres(request.getMpa(), request.getGenres());

        Film film = FilmMapper.mapToFilm(request);
        Film savedFilm = filmRepository.save(film);

        log.info("Фильм успешно создан с id: {}", savedFilm.getId());
        return FilmMapper.mapToFilmResponse(savedFilm);
    }

    public FilmResponse updateFilm(UpdateFilmRequest request) {
        log.info("Обновление фильма с id: {}", request.getId());

        if (request.getId() == null) {
            throw new NotFoundException("ID фильма должен быть указан");
        }

        Film film = filmRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + request.getId() + " не найден"));

        if (request.hasMpa() || request.hasGenres()) {
            validateMpaAndGenres(request.getMpa(), request.getGenres());
        }

        Film updatedFilm = FilmMapper.updateFilmFields(film, request);
        Film savedFilm = filmRepository.update(updatedFilm);

        log.info("Фильм с id {} успешно обновлен", savedFilm.getId());
        return FilmMapper.mapToFilmResponse(savedFilm);
    }

    public void addLike(int filmId, int userId) {
        log.info("Добавление лайка: фильм {} от пользователя {}", filmId, userId);

        filmRepository.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + filmId + " не найден"));
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        filmRepository.addLike(filmId, userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }

    public void removeLike(int filmId, int userId) {
        log.info("Удаление лайка: фильм {} от пользователя {}", filmId, userId);

        filmRepository.removeLike(filmId, userId);
        log.info("Пользователь {} удалил лайк у фильма {}", userId, filmId);
    }

    public List<FilmResponse> getPopular(int count) {
        log.info("Получение {} самых популярных фильмов", count);

        return filmRepository.findAll().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .map(FilmMapper::mapToFilmResponse)
                .collect(Collectors.toList());
    }

    private void validateMpaAndGenres(Mpa mpa, Set<Integer> genreIds) {
        if (mpa != null) {
            mpaRepository.findById(mpa.getId())
                    .orElseThrow(() -> new NotFoundException("Рейтинг MPA с id=" + mpa.getId() + " не найден"));
        }
        if (genreIds != null) {
            for (Integer genreId : genreIds) {
                genreRepository.findById(genreId)
                        .orElseThrow(() -> new NotFoundException("Жанр с id=" + genreId + " не найден"));
            }
        }
    }
}