package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryFilmStorageTest {

    private InMemoryFilmStorage filmStorage;
    private Film testFilm;
    private Mpa testMpa;
    private Genre testGenre1;
    private Genre testGenre2;
    private Set<Genre> testGenres;

    @BeforeEach
    void setUp() {
        filmStorage = new InMemoryFilmStorage();

        testMpa = new Mpa();
        testMpa.setId(1);
        testMpa.setName("PG-13");

        testGenre1 = new Genre();
        testGenre1.setId(1);
        testGenre1.setName("Комедия");

        testGenre2 = new Genre();
        testGenre2.setId(2);
        testGenre2.setName("Драма");

        testGenres = new LinkedHashSet<>();
        testGenres.add(testGenre1);
        testGenres.add(testGenre2);

        testFilm = new Film();
        testFilm.setName("Test Film");
        testFilm.setDescription("Test Description");
        testFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        testFilm.setDuration(120);
        testFilm.setMpa(testMpa);
        testFilm.setGenres(testGenres);
    }

    @Test
    void add_ShouldAssignIdAndStoreFilm() {
        Film added = filmStorage.add(testFilm);

        assertEquals(1, added.getId());
        assertEquals(testFilm.getName(), added.getName());
        assertEquals(testFilm.getMpa().getId(), added.getMpa().getId());
        assertEquals(testFilm.getGenres().size(), added.getGenres().size());

        Optional<Film> retrieved = filmStorage.getById(1);
        assertTrue(retrieved.isPresent());
        assertEquals(testFilm.getName(), retrieved.get().getName());
        assertNotNull(retrieved.get().getMpa());
        assertEquals(2, retrieved.get().getGenres().size());
    }

    @Test
    void add_ShouldIncrementId() {
        Film film1 = new Film();
        film1.setName("Film 1");
        film1.setDescription("Description 1");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(120);
        film1.setMpa(testMpa);
        film1.setGenres(testGenres);

        Film film2 = new Film();
        film2.setName("Film 2");
        film2.setDescription("Description 2");
        film2.setReleaseDate(LocalDate.of(2001, 1, 1));
        film2.setDuration(130);
        film2.setMpa(testMpa);
        film2.setGenres(testGenres);

        Film added1 = filmStorage.add(film1);
        Film added2 = filmStorage.add(film2);

        assertEquals(1, added1.getId());
        assertEquals(2, added2.getId());
    }

    @Test
    void update_ShouldUpdateExistingFilm() {
        Film added = filmStorage.add(testFilm);
        added.setName("Updated Name");

        Mpa newMpa = new Mpa();
        newMpa.setId(2);
        newMpa.setName("R");
        added.setMpa(newMpa);

        Set<Genre> newGenres = new LinkedHashSet<>();
        Genre newGenre = new Genre();
        newGenre.setId(3);
        newGenre.setName("Боевик");
        newGenres.add(newGenre);
        added.setGenres(newGenres);

        Film updated = filmStorage.update(added);

        assertEquals("Updated Name", updated.getName());
        assertEquals(2, updated.getMpa().getId());
        assertEquals(1, updated.getGenres().size());

        Optional<Film> retrieved = filmStorage.getById(added.getId());
        assertTrue(retrieved.isPresent());
        assertEquals("Updated Name", retrieved.get().getName());
        assertEquals("R", retrieved.get().getMpa().getName());
    }

    @Test
    void update_WhenFilmNotFound_ShouldThrowException() {
        testFilm.setId(999);

        assertThrows(NotFoundException.class, () -> filmStorage.update(testFilm));
    }

    @Test
    void delete_ShouldRemoveFilm() {
        Film added = filmStorage.add(testFilm);

        boolean result = filmStorage.delete(added.getId());

        assertTrue(result);
        assertTrue(filmStorage.getById(added.getId()).isEmpty());
    }

    @Test
    void delete_WhenFilmNotFound_ShouldReturnFalse() {
        boolean result = filmStorage.delete(999);

        assertFalse(result);
    }

    @Test
    void getAll_ShouldReturnAllFilms() {
        filmStorage.add(testFilm);

        Film secondFilm = new Film();
        secondFilm.setName("Second Film");
        secondFilm.setDescription("Second Description");
        secondFilm.setReleaseDate(LocalDate.of(2005, 5, 5));
        secondFilm.setDuration(150);
        secondFilm.setMpa(testMpa);
        secondFilm.setGenres(testGenres);

        filmStorage.add(secondFilm);

        List<Film> all = filmStorage.getAll();

        assertEquals(2, all.size());
        assertTrue(all.stream().allMatch(f -> f.getMpa() != null));
    }

    @Test
    void getById_ShouldReturnFilmWithAllFields() {
        Film added = filmStorage.add(testFilm);

        Optional<Film> retrieved = filmStorage.getById(added.getId());

        assertTrue(retrieved.isPresent());
        Film film = retrieved.get();
        assertEquals(testFilm.getName(), film.getName());
        assertNotNull(film.getMpa());
        assertEquals(testMpa.getId(), film.getMpa().getId());
        assertEquals(2, film.getGenres().size());
    }

    @Test
    void getById_ShouldReturnEmptyWhenNotFound() {
        Optional<Film> retrieved = filmStorage.getById(999);

        assertTrue(retrieved.isEmpty());
    }

    @Test
    void add_ShouldHandleFilmWithoutMpa() {
        Film filmWithoutMpa = new Film();
        filmWithoutMpa.setName("No MPA Film");
        filmWithoutMpa.setDescription("Description");
        filmWithoutMpa.setReleaseDate(LocalDate.of(2000, 1, 1));
        filmWithoutMpa.setDuration(120);
        filmWithoutMpa.setMpa(null);
        filmWithoutMpa.setGenres(new LinkedHashSet<>());

        Film added = filmStorage.add(filmWithoutMpa);

        assertNull(added.getMpa());
        assertTrue(added.getGenres().isEmpty());
    }

    @Test
    void add_ShouldHandleFilmWithoutGenres() {
        Film filmWithoutGenres = new Film();
        filmWithoutGenres.setName("No Genres Film");
        filmWithoutGenres.setDescription("Description");
        filmWithoutGenres.setReleaseDate(LocalDate.of(2000, 1, 1));
        filmWithoutGenres.setDuration(120);
        filmWithoutGenres.setMpa(testMpa);
        filmWithoutGenres.setGenres(new LinkedHashSet<>());

        Film added = filmStorage.add(filmWithoutGenres);

        assertNotNull(added.getMpa());
        assertTrue(added.getGenres().isEmpty());
    }
}