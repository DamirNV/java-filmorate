package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.NotFoundException;
import ru.yandex.practicum.filmorate.dal.repositories.GenreRepository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    public List<Genre> getAllGenres() {
        log.info("Получение всех жанров");
        return genreRepository.findAll();
    }

    public Genre getGenreById(int id) {
        log.info("Получение жанра по id: {}", id);
        return genreRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Жанр с id=" + id + " не найден"));
    }
}