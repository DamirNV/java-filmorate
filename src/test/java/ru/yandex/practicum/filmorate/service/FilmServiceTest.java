package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.controller.NotFoundException;
import ru.yandex.practicum.filmorate.dal.repositories.FilmRepository;
import ru.yandex.practicum.filmorate.dal.repositories.GenreRepository;
import ru.yandex.practicum.filmorate.dal.repositories.MpaRepository;
import ru.yandex.practicum.filmorate.dal.repositories.UserRepository;
import ru.yandex.practicum.filmorate.dto.film.CreateFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.FilmResponse;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class FilmServiceTest {

    @Mock
    private FilmRepository filmRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private MpaRepository mpaRepository;

    @InjectMocks
    private FilmService filmService;

    private Film film1;
    private Film film2;
    private Film film3;
    private Mpa mpa;
    private Genre genre1;
    private Genre genre2;
    private Set<Genre> genres;
    private User user;
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

        genreId1 = new CreateFilmRequest.GenreId();
        genreId1.setId(1);

        genreId2 = new CreateFilmRequest.GenreId();
        genreId2.setId(2);

        genreIds = new LinkedHashSet<>();
        genreIds.add(genreId1);
        genreIds.add(genreId2);

        film1 = new Film();
        film1.setId(1);
        film1.setName("Film 1");
        film1.setDescription("Description 1");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(120);
        film1.setLikes(new HashSet<>());
        film1.setMpa(mpa);
        film1.setGenres(genres);

        film2 = new Film();
        film2.setId(2);
        film2.setName("Film 2");
        film2.setDescription("Description 2");
        film2.setReleaseDate(LocalDate.of(2001, 2, 2));
        film2.setDuration(130);
        film2.setLikes(new HashSet<>());
        film2.setMpa(mpa);
        film2.setGenres(new LinkedHashSet<>());

        film3 = new Film();
        film3.setId(3);
        film3.setName("Film 3");
        film3.setDescription("Description 3");
        film3.setReleaseDate(LocalDate.of(2002, 3, 3));
        film3.setDuration(140);
        film3.setLikes(new HashSet<>());
        film3.setMpa(mpa);
        film3.setGenres(new LinkedHashSet<>());

        user = new User();
        user.setId(100);
        user.setEmail("user@test.com");
        user.setLogin("user");
        user.setName("User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

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
        updateRequest.setReleaseDate(LocalDate.of(2000, 1, 1));
        updateRequest.setDuration(120);
        updateRequest.setMpa(mpa);
        updateRequest.setGenres(genreIds);
    }

    @Test
    void getAllFilms_ShouldReturnAllFilms() {
        when(filmRepository.findAll()).thenReturn(List.of(film1, film2));

        List<FilmResponse> result = filmService.getAllFilms();

        assertEquals(2, result.size());
        assertEquals("Film 1", result.get(0).getName());
        verify(filmRepository, times(1)).findAll();
    }

    @Test
    void getFilmById_ShouldReturnFilm() {
        when(filmRepository.findById(1)).thenReturn(Optional.of(film1));

        FilmResponse result = filmService.getFilmById(1);

        assertEquals("Film 1", result.getName());
        assertEquals(2, result.getGenres().size());
        verify(filmRepository, times(1)).findById(1);
    }

    @Test
    void getFilmById_WhenNotFound_ShouldThrowException() {
        when(filmRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> filmService.getFilmById(999));
    }

    @Test
    void createFilm_ShouldCreateAndReturnFilm() {
        when(mpaRepository.findById(1)).thenReturn(Optional.of(mpa));
        when(genreRepository.findById(1)).thenReturn(Optional.of(genre1));
        when(genreRepository.findById(2)).thenReturn(Optional.of(genre2));
        when(filmRepository.save(any(Film.class))).thenReturn(film1);

        FilmResponse result = filmService.createFilm(createRequest);

        assertNotNull(result);
        assertEquals("Film 1", result.getName());
        verify(mpaRepository, times(1)).findById(1);
        verify(genreRepository, times(1)).findById(1);
        verify(genreRepository, times(1)).findById(2);
        verify(filmRepository, times(1)).save(any(Film.class));
    }

    @Test
    void createFilm_WhenMpaNotFound_ShouldThrowException() {
        when(mpaRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> filmService.createFilm(createRequest));
        verify(filmRepository, never()).save(any(Film.class));
    }

    @Test
    void createFilm_WhenGenreNotFound_ShouldThrowException() {
        when(mpaRepository.findById(1)).thenReturn(Optional.of(mpa));
        when(genreRepository.findById(1)).thenReturn(Optional.of(genre1));
        when(genreRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> filmService.createFilm(createRequest));
        verify(filmRepository, never()).save(any(Film.class));
    }

    @Test
    void createFilm_WithoutGenres_ShouldCreateFilm() {
        createRequest.setGenres(null);

        when(mpaRepository.findById(1)).thenReturn(Optional.of(mpa));
        when(filmRepository.save(any(Film.class))).thenReturn(film1);

        FilmResponse result = filmService.createFilm(createRequest);

        assertNotNull(result);
        verify(genreRepository, never()).findById(anyInt());
        verify(filmRepository, times(1)).save(any(Film.class));
    }

    @Test
    void updateFilm_ShouldUpdateAndReturnFilm() {
        when(filmRepository.findById(1)).thenReturn(Optional.of(film1));
        when(mpaRepository.findById(1)).thenReturn(Optional.of(mpa));
        when(genreRepository.findById(1)).thenReturn(Optional.of(genre1));
        when(genreRepository.findById(2)).thenReturn(Optional.of(genre2));
        when(filmRepository.update(any(Film.class))).thenReturn(film1);

        FilmResponse result = filmService.updateFilm(updateRequest);

        assertNotNull(result);
        verify(filmRepository, times(1)).findById(1);
        verify(filmRepository, times(1)).update(any(Film.class));
    }

    @Test
    void updateFilm_WhenFilmNotFound_ShouldThrowException() {
        when(filmRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> filmService.updateFilm(updateRequest));
        verify(filmRepository, never()).update(any(Film.class));
    }

    @Test
    void updateFilm_WithoutId_ShouldThrowException() {
        updateRequest.setId(null);

        assertThrows(NotFoundException.class, () -> filmService.updateFilm(updateRequest));
    }

    @Test
    void addLike_ShouldAddLike() {
        when(filmRepository.findById(1)).thenReturn(Optional.of(film1));
        when(userRepository.findById(100)).thenReturn(Optional.of(user));
        doNothing().when(filmRepository).addLike(1, 100);

        filmService.addLike(1, 100);

        verify(filmRepository, times(1)).addLike(1, 100);
    }

    @Test
    void addLike_WhenFilmNotFound_ShouldThrowException() {
        when(filmRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> filmService.addLike(1, 100));
        verify(filmRepository, never()).addLike(anyInt(), anyInt());
    }

    @Test
    void addLike_WhenUserNotFound_ShouldThrowException() {
        when(filmRepository.findById(1)).thenReturn(Optional.of(film1));
        when(userRepository.findById(100)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> filmService.addLike(1, 100));
        verify(filmRepository, never()).addLike(anyInt(), anyInt());
    }

    @Test
    void removeLike_ShouldRemoveLike() {
        lenient().when(filmRepository.findById(1)).thenReturn(Optional.of(film1));
        doNothing().when(filmRepository).removeLike(1, 100);

        filmService.removeLike(1, 100);

        verify(filmRepository, times(1)).removeLike(1, 100);
    }

    @Test
    void getPopular_ShouldReturnFilmsSortedByLikes() {
        film1.getLikes().addAll(Set.of(1, 2, 3));
        film2.getLikes().addAll(Set.of(4, 5));
        film3.getLikes().addAll(Set.of(6));

        when(filmRepository.findAll()).thenReturn(List.of(film1, film2, film3));

        List<FilmResponse> popular = filmService.getPopular(3);

        assertEquals(3, popular.size());
        assertEquals("Film 1", popular.get(0).getName());
        assertEquals("Film 2", popular.get(1).getName());
        assertEquals("Film 3", popular.get(2).getName());
    }

    @Test
    void getPopular_WithLimit_ShouldReturnLimitedList() {
        film1.getLikes().addAll(Set.of(1, 2, 3));
        film2.getLikes().addAll(Set.of(4, 5));
        film3.getLikes().addAll(Set.of(6));

        when(filmRepository.findAll()).thenReturn(List.of(film1, film2, film3));

        List<FilmResponse> popular = filmService.getPopular(2);

        assertEquals(2, popular.size());
        assertEquals("Film 1", popular.get(0).getName());
        assertEquals("Film 2", popular.get(1).getName());
    }

    @Test
    void getPopular_WhenNoLikes_ShouldReturnAllFilms() {
        when(filmRepository.findAll()).thenReturn(List.of(film1, film2, film3));

        List<FilmResponse> popular = filmService.getPopular(10);

        assertEquals(3, popular.size());
    }
}