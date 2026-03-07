package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmTest {

    private Film film;
    private Mpa mpa;
    private Genre genre1;
    private Genre genre2;
    private Set<Genre> genres;

    @BeforeEach
    void setUp() {
        mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("PG-13");

        genre1 = new Genre();
        genre1.setId(1);
        genre1.setName("Комедия");

        genre2 = new Genre();
        genre2.setId(2);
        genre2.setName("Драма");

        genres = new LinkedHashSet<>();
        genres.add(genre1);
        genres.add(genre2);

        film = new Film();
        film.setId(1);
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(mpa);
        film.setGenres(genres);
        film.setLikes(Set.of(1, 2, 3));
    }

    @Test
    void constructorAndGetters_ShouldWork() {
        assertEquals(1, film.getId());
        assertEquals("Test Film", film.getName());
        assertEquals("Test Description", film.getDescription());
        assertEquals(LocalDate.of(2000, 1, 1), film.getReleaseDate());
        assertEquals(120, film.getDuration());
        assertEquals(mpa, film.getMpa());
        assertEquals(2, film.getGenres().size());
        assertEquals(3, film.getLikes().size());
    }

    @Test
    void setters_ShouldWork() {
        film.setId(2);
        film.setName("New Name");
        film.setDescription("New Description");
        film.setReleaseDate(LocalDate.of(2001, 1, 1));
        film.setDuration(130);

        Mpa newMpa = new Mpa();
        newMpa.setId(2);
        newMpa.setName("R");
        film.setMpa(newMpa);

        assertEquals(2, film.getId());
        assertEquals("New Name", film.getName());
        assertEquals("New Description", film.getDescription());
        assertEquals(LocalDate.of(2001, 1, 1), film.getReleaseDate());
        assertEquals(130, film.getDuration());
        assertEquals(newMpa, film.getMpa());
    }

    @Test
    void likes_ShouldBeMutable() {
        film.getLikes().add(4);
        assertEquals(4, film.getLikes().size());
        assertTrue(film.getLikes().contains(4));

        film.getLikes().remove(1);
        assertEquals(3, film.getLikes().size());
        assertFalse(film.getLikes().contains(1));
    }

    @Test
    void genres_ShouldBeMutable() {
        Genre genre3 = new Genre();
        genre3.setId(3);
        genre3.setName("Мультфильм");

        film.getGenres().add(genre3);
        assertEquals(3, film.getGenres().size());
        assertTrue(film.getGenres().contains(genre3));

        film.getGenres().remove(genre1);
        assertEquals(2, film.getGenres().size());
        assertFalse(film.getGenres().contains(genre1));
    }

    @Test
    void equals_SameId_ShouldReturnTrue() {
        Film film2 = new Film();
        film2.setId(1);
        film2.setName("Different Name");

        assertEquals(film, film2);
    }

    @Test
    void equals_DifferentId_ShouldReturnFalse() {
        Film film2 = new Film();
        film2.setId(2);
        film2.setName("Test Film");

        assertNotEquals(film, film2);
    }

    @Test
    void equals_SameObject_ShouldReturnTrue() {
        assertEquals(film, film);
    }

    @Test
    void equals_Null_ShouldReturnFalse() {
        assertNotEquals(null, film);
    }

    @Test
    void hashCode_SameId_ShouldBeEqual() {
        Film film2 = new Film();
        film2.setId(1);

        assertEquals(film.hashCode(), film2.hashCode());
    }

    @Test
    void hashCode_DifferentId_ShouldBeDifferent() {
        Film film2 = new Film();
        film2.setId(2);

        assertNotEquals(film.hashCode(), film2.hashCode());
    }

    @Test
    void toString_ShouldNotBeEmpty() {
        assertNotNull(film.toString());
        assertFalse(film.toString().isEmpty());
    }
}