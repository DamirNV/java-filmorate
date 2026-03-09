package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.film.CreateFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.FilmResponse;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {

    public static Film mapToFilm(CreateFilmRequest request) {
        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());
        film.setMpa(request.getMpa());

        if (request.getGenres() != null) {
            Set<Genre> genres = request.getGenres().stream()
                    .map(genreId -> {
                        Genre genre = new Genre();
                        genre.setId(genreId.getId());
                        return genre;
                    })
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            film.setGenres(genres);
        }

        return film;
    }

    public static Film updateFilmFields(Film film, UpdateFilmRequest request) {
        if (request.hasName()) {
            film.setName(request.getName());
        }
        if (request.hasDescription()) {
            film.setDescription(request.getDescription());
        }
        if (request.hasReleaseDate()) {
            film.setReleaseDate(request.getReleaseDate());
        }
        if (request.hasDuration()) {
            film.setDuration(request.getDuration());
        }
        if (request.hasMpa()) {
            film.setMpa(request.getMpa());
        }
        if (request.hasGenres()) {
            Set<Genre> genres = request.getGenres().stream()
                    .map(genreId -> {
                        Genre genre = new Genre();
                        genre.setId(genreId.getId());
                        return genre;
                    })
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            film.setGenres(genres);
        }
        return film;
    }

    public static FilmResponse mapToFilmResponse(Film film) {
        FilmResponse response = new FilmResponse();
        response.setId((long) film.getId());
        response.setName(film.getName());
        response.setDescription(film.getDescription());
        response.setReleaseDate(film.getReleaseDate());
        response.setDuration(film.getDuration());
        response.setMpa(film.getMpa());
        response.setGenres(film.getGenres());
        response.setLikes(film.getLikes());
        return response;
    }
}