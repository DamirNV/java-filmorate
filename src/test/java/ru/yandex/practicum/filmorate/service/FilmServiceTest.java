package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.controller.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmServiceTest {

    @Mock
    private FilmStorage filmStorage;

    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private FilmService filmService;

    private Film film1;
    private Film film2;
    private Film film3;

    @BeforeEach
    void setUp() {
        film1 = new Film();
        film1.setId(1);
        film1.setName("Film 1");
        film1.setDescription("Description 1");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(120);
        film1.setLikes(new HashSet<>());

        film2 = new Film();
        film2.setId(2);
        film2.setName("Film 2");
        film2.setDescription("Description 2");
        film2.setReleaseDate(LocalDate.of(2001, 2, 2));
        film2.setDuration(130);
        film2.setLikes(new HashSet<>());

        film3 = new Film();
        film3.setId(3);
        film3.setName("Film 3");
        film3.setDescription("Description 3");
        film3.setReleaseDate(LocalDate.of(2002, 3, 3));
        film3.setDuration(140);
        film3.setLikes(new HashSet<>());
    }

    @Test
    void addLike_ShouldAddUserIdToLikes() {
        when(filmStorage.getById(1)).thenReturn(Optional.of(film1));
        when(userStorage.getById(100)).thenReturn(Optional.of(mock(ru.yandex.practicum.filmorate.model.User.class)));

        filmService.addLike(1, 100);

        assertTrue(film1.getLikes().contains(100));
        assertEquals(1, film1.getLikes().size());
    }

    @Test
    void addLike_WhenFilmNotFound_ShouldThrowException() {
        when(filmStorage.getById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> filmService.addLike(1, 100));
        verify(filmStorage, times(1)).getById(1);
        verify(userStorage, never()).getById(anyInt());
    }

    @Test
    void addLike_WhenUserNotFound_ShouldThrowException() {
        when(filmStorage.getById(1)).thenReturn(Optional.of(film1));
        when(userStorage.getById(100)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> filmService.addLike(1, 100));
        assertFalse(film1.getLikes().contains(100));
    }

    @Test
    void addLike_WhenAlreadyLiked_ShouldNotDuplicate() {
        film1.getLikes().add(100);

        when(filmStorage.getById(1)).thenReturn(Optional.of(film1));
        when(userStorage.getById(100)).thenReturn(Optional.of(mock(ru.yandex.practicum.filmorate.model.User.class)));

        filmService.addLike(1, 100);

        assertEquals(1, film1.getLikes().size());
    }

    @Test
    void removeLike_ShouldRemoveUserIdFromLikes() {
        film1.getLikes().add(100);

        when(filmStorage.getById(1)).thenReturn(Optional.of(film1));
        when(userStorage.getById(100)).thenReturn(Optional.of(mock(ru.yandex.practicum.filmorate.model.User.class)));

        filmService.removeLike(1, 100);

        assertFalse(film1.getLikes().contains(100));
    }

    @Test
    void removeLike_WhenNotLiked_ShouldDoNothing() {
        when(filmStorage.getById(1)).thenReturn(Optional.of(film1));
        when(userStorage.getById(100)).thenReturn(Optional.of(mock(ru.yandex.practicum.filmorate.model.User.class)));

        filmService.removeLike(1, 100);

        assertTrue(film1.getLikes().isEmpty());
    }

    @Test
    void getPopular_ShouldReturnFilmsSortedByLikesDesc() {
        film1.getLikes().addAll(Set.of(1, 2, 3)); // 3 лайка
        film2.getLikes().addAll(Set.of(4, 5));   // 2 лайка
        film3.getLikes().addAll(Set.of(6));      // 1 лайк

        when(filmStorage.getAll()).thenReturn(List.of(film1, film2, film3));

        List<Film> popular = filmService.getPopular(3);

        assertEquals(3, popular.size());
        assertEquals(film1, popular.get(0)); // больше всего лайков
        assertEquals(film2, popular.get(1));
        assertEquals(film3, popular.get(2));
    }

    @Test
    void getPopular_ShouldRespectCountParameter() {
        film1.getLikes().addAll(Set.of(1, 2, 3));
        film2.getLikes().addAll(Set.of(4, 5));
        film3.getLikes().addAll(Set.of(6));

        when(filmStorage.getAll()).thenReturn(List.of(film1, film2, film3));

        List<Film> popular = filmService.getPopular(2);

        assertEquals(2, popular.size());
        assertEquals(film1, popular.get(0));
        assertEquals(film2, popular.get(1));
    }

    @Test
    void getPopular_WhenCountMoreThanFilms_ShouldReturnAllFilms() {
        when(filmStorage.getAll()).thenReturn(List.of(film1, film2, film3));

        List<Film> popular = filmService.getPopular(10);

        assertEquals(3, popular.size());
    }

    @Test
    void getPopular_WhenNoLikes_ShouldReturnAllFilms() {
        when(filmStorage.getAll()).thenReturn(List.of(film1, film2, film3));

        List<Film> popular = filmService.getPopular(5);

        assertEquals(3, popular.size());
    }
}
