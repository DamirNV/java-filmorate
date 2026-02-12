package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryFilmStorageTest {

    private InMemoryFilmStorage filmStorage;
    private Film testFilm;

    @BeforeEach
    void setUp() {
        filmStorage = new InMemoryFilmStorage();
        testFilm = new Film();
        testFilm.setName("Test Film");
        testFilm.setDescription("Test Description");
        testFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        testFilm.setDuration(120);
    }

    @Test
    void add_ShouldAssignIdAndStoreFilm() {
        Film added = filmStorage.add(testFilm);

        assertEquals(1, added.getId());
        assertEquals(testFilm.getName(), added.getName());

        Optional<Film> retrieved = filmStorage.getById(1);
        assertTrue(retrieved.isPresent());
        assertEquals(testFilm.getName(), retrieved.get().getName());
    }

    @Test
    void add_ShouldIncrementId() {
        Film film1 = filmStorage.add(testFilm);
        Film film2 = filmStorage.add(testFilm);

        assertEquals(1, film1.getId());
        assertEquals(2, film2.getId());
    }

    @Test
    void update_ShouldUpdateExistingFilm() {
        Film added = filmStorage.add(testFilm);
        added.setName("Updated Name");

        Film updated = filmStorage.update(added);

        assertEquals("Updated Name", updated.getName());
        Optional<Film> retrieved = filmStorage.getById(added.getId());
        assertTrue(retrieved.isPresent());
        assertEquals("Updated Name", retrieved.get().getName());
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
        filmStorage.add(testFilm);

        List<Film> all = filmStorage.getAll();

        assertEquals(2, all.size());
    }

    @Test
    void getById_ShouldReturnEmptyWhenNotFound() {
        Optional<Film> retrieved = filmStorage.getById(999);

        assertTrue(retrieved.isEmpty());
    }
}
