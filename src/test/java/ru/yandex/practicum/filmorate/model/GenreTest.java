package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GenreTest {

    @Test
    void equals_SameId_ShouldReturnTrue() {
        Genre genre1 = new Genre();
        genre1.setId(1);
        genre1.setName("Комедия");

        Genre genre2 = new Genre();
        genre2.setId(1);
        genre2.setName("Драма");

        assertEquals(genre1, genre2);
    }

    @Test
    void equals_DifferentId_ShouldReturnFalse() {
        Genre genre1 = new Genre();
        genre1.setId(1);
        genre1.setName("Комедия");

        Genre genre2 = new Genre();
        genre2.setId(2);
        genre2.setName("Комедия");

        assertNotEquals(genre1, genre2);
    }

    @Test
    void equals_SameObject_ShouldReturnTrue() {
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");

        assertEquals(genre, genre);
    }

    @Test
    void equals_Null_ShouldReturnFalse() {
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");

        assertNotEquals(null, genre);
    }

    @Test
    void equals_DifferentClass_ShouldReturnFalse() {
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");

        String notAGenre = "not a genre";
        assertNotEquals(genre, notAGenre);
    }

    @Test
    void hashCode_SameId_ShouldBeEqual() {
        Genre genre1 = new Genre();
        genre1.setId(1);
        genre1.setName("Комедия");

        Genre genre2 = new Genre();
        genre2.setId(1);
        genre2.setName("Драма");

        assertEquals(genre1.hashCode(), genre2.hashCode());
    }

    @Test
    void hashCode_DifferentId_ShouldBeDifferent() {
        Genre genre1 = new Genre();
        genre1.setId(1);
        genre1.setName("Комедия");

        Genre genre2 = new Genre();
        genre2.setId(2);
        genre2.setName("Комедия");

        assertNotEquals(genre1.hashCode(), genre2.hashCode());
    }

    @Test
    void constructorAndGetters_ShouldWork() {
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");

        assertEquals(1, genre.getId());
        assertEquals("Комедия", genre.getName());
    }

    @Test
    void toString_ShouldNotBeEmpty() {
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");

        assertNotNull(genre.toString());
        assertFalse(genre.toString().isEmpty());
    }
}
