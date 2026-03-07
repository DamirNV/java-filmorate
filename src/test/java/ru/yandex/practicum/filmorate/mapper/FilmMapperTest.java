package ru.yandex.practicum.filmorate.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.film.CreateFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.FilmResponse;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmMapperTest {

    private Mpa mpa;
    private Genre genre1;
    private Genre genre2;
    private Set<Genre> genres;
    private Film film;
    private CreateFilmRequest createRequest;
    private UpdateFilmRequest updateRequest;
    private CreateFilmRequest.GenreId genreId1;
    private CreateFilmRequest.GenreId genreId2;
    private Set<CreateFilmRequest.GenreId> genreIds;

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

        genreId1 = new CreateFilmRequest.GenreId();
        genreId1.setId(1);

        genreId2 = new CreateFilmRequest.GenreId();
        genreId2.setId(2);

        genreIds = new LinkedHashSet<>();
        genreIds.add(genreId1);
        genreIds.add(genreId2);

        createRequest = new CreateFilmRequest();
        createRequest.setName("New Film");
        createRequest.setDescription("New Description");
        createRequest.setReleaseDate(LocalDate.of(2000, 1, 1));
        createRequest.setDuration(120);
        createRequest.setMpa(mpa);
        createRequest.setGenres(genreIds);

        updateRequest = new UpdateFilmRequest();
        updateRequest.setId(1L);
        updateRequest.setName("Updated Film");
        updateRequest.setDescription("Updated Description");
        updateRequest.setReleaseDate(LocalDate.of(2001, 1, 1));
        updateRequest.setDuration(130);
        updateRequest.setMpa(mpa);
        updateRequest.setGenres(genreIds);
    }

    @Test
    void mapToFilm_FromCreateRequest_ShouldMapAllFields() {
        Film result = FilmMapper.mapToFilm(createRequest);

        assertEquals("New Film", result.getName());
        assertEquals("New Description", result.getDescription());
        assertEquals(LocalDate.of(2000, 1, 1), result.getReleaseDate());
        assertEquals(120, result.getDuration());
        assertEquals(mpa, result.getMpa());
        assertEquals(2, result.getGenres().size());

        assertTrue(result.getGenres().stream().anyMatch(g -> g.getId() == 1));
        assertTrue(result.getGenres().stream().anyMatch(g -> g.getId() == 2));
    }

    @Test
    void mapToFilm_FromCreateRequest_WithoutGenres_ShouldSetEmptyGenres() {
        createRequest.setGenres(null);

        Film result = FilmMapper.mapToFilm(createRequest);

        assertNotNull(result.getGenres());
        assertTrue(result.getGenres().isEmpty());
    }

    @Test
    void mapToFilm_FromCreateRequest_WithEmptyGenres_ShouldSetEmptyGenres() {
        createRequest.setGenres(new LinkedHashSet<>());

        Film result = FilmMapper.mapToFilm(createRequest);

        assertNotNull(result.getGenres());
        assertTrue(result.getGenres().isEmpty());
    }

    @Test
    void updateFilmFields_ShouldUpdateOnlyProvidedFields() {
        Film result = FilmMapper.updateFilmFields(film, updateRequest);

        assertEquals("Updated Film", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(LocalDate.of(2001, 1, 1), result.getReleaseDate());
        assertEquals(130, result.getDuration());
        assertEquals(mpa, result.getMpa());
        assertEquals(2, result.getGenres().size());
    }

    @Test
    void updateFilmFields_WithNullFields_ShouldKeepOriginalValues() {
        updateRequest.setName(null);
        updateRequest.setDescription(null);
        updateRequest.setReleaseDate(null);
        updateRequest.setDuration(null);
        updateRequest.setMpa(null);
        updateRequest.setGenres(null);

        Film result = FilmMapper.updateFilmFields(film, updateRequest);

        assertEquals("Test Film", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertEquals(LocalDate.of(2000, 1, 1), result.getReleaseDate());
        assertEquals(120, result.getDuration());
        assertEquals(mpa, result.getMpa());
        assertEquals(2, result.getGenres().size());
    }

    @Test
    void updateFilmFields_WithEmptyName_ShouldNotUpdate() {
        updateRequest.setName("");

        Film result = FilmMapper.updateFilmFields(film, updateRequest);

        assertEquals("Test Film", result.getName());
    }

    @Test
    void updateFilmFields_WithBlankName_ShouldNotUpdate() {
        updateRequest.setName("   ");

        Film result = FilmMapper.updateFilmFields(film, updateRequest);

        assertEquals("Test Film", result.getName());
    }

    @Test
    void updateFilmFields_WithEmptyDescription_ShouldNotUpdate() {
        updateRequest.setDescription("");

        Film result = FilmMapper.updateFilmFields(film, updateRequest);

        assertEquals("Test Description", result.getDescription());
    }

    @Test
    void updateFilmFields_WithEmptyGenres_ShouldUpdateToEmpty() {
        updateRequest.setGenres(new LinkedHashSet<>());
        Film result = FilmMapper.updateFilmFields(film, updateRequest);
        assertTrue(result.getGenres() == null || result.getGenres().isEmpty());
    }

    @Test
    void mapToFilmResponse_ShouldMapAllFields() {
        FilmResponse result = FilmMapper.mapToFilmResponse(film);

        assertEquals(1L, result.getId());
        assertEquals("Test Film", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertEquals(LocalDate.of(2000, 1, 1), result.getReleaseDate());
        assertEquals(120, result.getDuration());
        assertEquals(mpa, result.getMpa());
        assertEquals(2, result.getGenres().size());
        assertEquals(3, result.getLikes().size());
        assertTrue(result.getLikes().contains(1));
        assertTrue(result.getLikes().contains(2));
        assertTrue(result.getLikes().contains(3));
    }

    @Test
    void mapToFilmResponse_WithoutLikes_ShouldSetEmptyLikes() {
        film.setLikes(null);

        FilmResponse result = FilmMapper.mapToFilmResponse(film);
        assertTrue(result.getLikes() == null || result.getLikes().isEmpty());
    }

    @Test
    void mapToFilmResponse_WithoutGenres_ShouldSetEmptyGenres() {
        film.setGenres(null);

        FilmResponse result = FilmMapper.mapToFilmResponse(film);
        assertTrue(result.getGenres() == null || result.getGenres().isEmpty());
    }

    @Test
    void mapToFilmResponse_ShouldPreserveGenreOrder() {
        FilmResponse result = FilmMapper.mapToFilmResponse(film);

        Genre[] genresArray = result.getGenres().toArray(new Genre[0]);
        assertEquals(1, genresArray[0].getId());
        assertEquals(2, genresArray[1].getId());
    }

    @Test
    void mapToFilm_ShouldPreserveGenreOrder() {
        Film result = FilmMapper.mapToFilm(createRequest);

        Genre[] genresArray = result.getGenres().toArray(new Genre[0]);
        assertEquals(1, genresArray[0].getId());
        assertEquals(2, genresArray[1].getId());
    }

    @Test
    void updateFilmFields_ShouldPreserveGenreOrder() {
        Film result = FilmMapper.updateFilmFields(film, updateRequest);

        Genre[] genresArray = result.getGenres().toArray(new Genre[0]);
        assertEquals(1, genresArray[0].getId());
        assertEquals(2, genresArray[1].getId());
    }
}