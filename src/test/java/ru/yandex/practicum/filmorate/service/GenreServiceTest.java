package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.controller.NotFoundException;
import ru.yandex.practicum.filmorate.dal.repositories.GenreRepository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private GenreService genreService;

    private Genre comedy;
    private Genre drama;
    private Genre cartoon;
    private Genre thriller;
    private Genre documentary;
    private Genre action;

    @BeforeEach
    void setUp() {
        comedy = new Genre();
        comedy.setId(1);
        comedy.setName("Комедия");

        drama = new Genre();
        drama.setId(2);
        drama.setName("Драма");

        cartoon = new Genre();
        cartoon.setId(3);
        cartoon.setName("Мультфильм");

        thriller = new Genre();
        thriller.setId(4);
        thriller.setName("Триллер");

        documentary = new Genre();
        documentary.setId(5);
        documentary.setName("Документальный");

        action = new Genre();
        action.setId(6);
        action.setName("Боевик");
    }

    @Test
    void getAllGenres_ShouldReturnAllGenres() {
        List<Genre> expectedGenres = List.of(comedy, drama, cartoon, thriller, documentary, action);
        when(genreRepository.findAll()).thenReturn(expectedGenres);

        List<Genre> result = genreService.getAllGenres();

        assertEquals(6, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals("Комедия", result.get(0).getName());
        assertEquals(2, result.get(1).getId());
        assertEquals("Драма", result.get(1).getName());
        assertEquals(3, result.get(2).getId());
        assertEquals("Мультфильм", result.get(2).getName());
        assertEquals(4, result.get(3).getId());
        assertEquals("Триллер", result.get(3).getName());
        assertEquals(5, result.get(4).getId());
        assertEquals("Документальный", result.get(4).getName());
        assertEquals(6, result.get(5).getId());
        assertEquals("Боевик", result.get(5).getName());

        verify(genreRepository, times(1)).findAll();
    }

    @Test
    void getAllGenres_WhenEmpty_ShouldReturnEmptyList() {
        when(genreRepository.findAll()).thenReturn(List.of());

        List<Genre> result = genreService.getAllGenres();

        assertTrue(result.isEmpty());
        verify(genreRepository, times(1)).findAll();
    }

    @Test
    void getGenreById_ShouldReturnGenre() {
        when(genreRepository.findById(1)).thenReturn(Optional.of(comedy));

        Genre result = genreService.getGenreById(1);

        assertEquals(1, result.getId());
        assertEquals("Комедия", result.getName());
        verify(genreRepository, times(1)).findById(1);
    }

    @Test
    void getGenreById_WhenNotFound_ShouldThrowException() {
        when(genreRepository.findById(999)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> genreService.getGenreById(999));

        assertEquals("Жанр с id=999 не найден", exception.getMessage());
        verify(genreRepository, times(1)).findById(999);
    }

    @Test
    void getGenreById_ShouldReturnCorrectGenreForEachId() {
        when(genreRepository.findById(1)).thenReturn(Optional.of(comedy));
        when(genreRepository.findById(2)).thenReturn(Optional.of(drama));
        when(genreRepository.findById(3)).thenReturn(Optional.of(cartoon));
        when(genreRepository.findById(4)).thenReturn(Optional.of(thriller));
        when(genreRepository.findById(5)).thenReturn(Optional.of(documentary));
        when(genreRepository.findById(6)).thenReturn(Optional.of(action));

        Genre result1 = genreService.getGenreById(1);
        assertEquals("Комедия", result1.getName());

        Genre result2 = genreService.getGenreById(2);
        assertEquals("Драма", result2.getName());

        Genre result3 = genreService.getGenreById(3);
        assertEquals("Мультфильм", result3.getName());

        Genre result4 = genreService.getGenreById(4);
        assertEquals("Триллер", result4.getName());

        Genre result5 = genreService.getGenreById(5);
        assertEquals("Документальный", result5.getName());

        Genre result6 = genreService.getGenreById(6);
        assertEquals("Боевик", result6.getName());

        verify(genreRepository, times(1)).findById(1);
        verify(genreRepository, times(1)).findById(2);
        verify(genreRepository, times(1)).findById(3);
        verify(genreRepository, times(1)).findById(4);
        verify(genreRepository, times(1)).findById(5);
        verify(genreRepository, times(1)).findById(6);
    }
}
