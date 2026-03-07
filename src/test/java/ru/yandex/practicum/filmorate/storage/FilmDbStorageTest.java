package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@ContextConfiguration(classes = {FilmDbStorage.class})
@Sql(statements = {
        "INSERT INTO mpa_rating (mpa_rating_id, code) VALUES (1, 'G'), (2, 'PG')",
        "INSERT INTO genres (genre_id, name) VALUES (1, 'Комедия'), (2, 'Драма')"
})
class FilmDbStorageTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private FilmDbStorage filmStorage;
    private Mpa testMpa;
    private Set<Genre> testGenres;

    @BeforeEach
    void setUp() {
        filmStorage = new FilmDbStorage(jdbcTemplate);

        testMpa = new Mpa();
        testMpa.setId(1);
        testMpa.setName("G");

        Genre genre1 = new Genre();
        genre1.setId(1);
        genre1.setName("Комедия");

        Genre genre2 = new Genre();
        genre2.setId(2);
        genre2.setName("Драма");

        testGenres = new LinkedHashSet<>();
        testGenres.add(genre1);
        testGenres.add(genre2);
    }

    @Test
    void testAddFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(testMpa);
        film.setGenres(testGenres);

        Film created = filmStorage.add(film);

        assertThat(created.getId()).isPositive();
        assertThat(created.getName()).isEqualTo("Test Film");
        assertThat(created.getGenres()).hasSize(2);
    }

    @Test
    void testGetFilmById() {
        Film film = new Film();
        film.setName("Get Film");
        film.setDescription("Get Description");
        film.setReleaseDate(LocalDate.of(2001, 2, 2));
        film.setDuration(130);
        film.setMpa(testMpa);
        film.setGenres(testGenres);

        Film created = filmStorage.add(film);
        Optional<Film> found = filmStorage.getById(created.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Get Film");
        assertThat(found.get().getGenres()).hasSize(2);
    }

    @Test
    void testUpdateFilm() {
        Film film = new Film();
        film.setName("Update Film");
        film.setDescription("Update Description");
        film.setReleaseDate(LocalDate.of(2002, 3, 3));
        film.setDuration(140);
        film.setMpa(testMpa);
        film.setGenres(testGenres);

        Film created = filmStorage.add(film);
        created.setName("Updated Name");

        Set<Genre> singleGenre = new LinkedHashSet<>();
        Genre genre1 = new Genre();
        genre1.setId(1);
        genre1.setName("Комедия");
        singleGenre.add(genre1);
        created.setGenres(singleGenre);

        Film updated = filmStorage.update(created);

        assertThat(updated.getName()).isEqualTo("Updated Name");
        assertThat(updated.getGenres()).hasSize(1);

        Optional<Film> found = filmStorage.getById(created.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Updated Name");
        assertThat(found.get().getGenres()).hasSize(1);
    }

    @Test
    void testDeleteFilm() {
        Film film = new Film();
        film.setName("Delete Film");
        film.setDescription("Delete Description");
        film.setReleaseDate(LocalDate.of(2003, 4, 4));
        film.setDuration(150);
        film.setMpa(testMpa);
        film.setGenres(testGenres);

        Film created = filmStorage.add(film);
        boolean deleted = filmStorage.delete(created.getId());

        assertTrue(deleted);
        Optional<Film> found = filmStorage.getById(created.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void testGetAllFilms() {
        Film film1 = new Film();
        film1.setName("Film 1");
        film1.setDescription("Description 1");
        film1.setReleaseDate(LocalDate.of(2004, 5, 5));
        film1.setDuration(160);
        film1.setMpa(testMpa);
        film1.setGenres(testGenres);

        Film film2 = new Film();
        film2.setName("Film 2");
        film2.setDescription("Description 2");
        film2.setReleaseDate(LocalDate.of(2005, 6, 6));
        film2.setDuration(170);
        film2.setMpa(testMpa);
        film2.setGenres(testGenres);

        filmStorage.add(film1);
        filmStorage.add(film2);

        List<Film> films = filmStorage.getAll();

        assertThat(films).hasSize(2);
    }
}
