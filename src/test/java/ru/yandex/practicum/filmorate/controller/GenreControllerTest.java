package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GenreController.class)
class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenreService genreService;

    private Genre comedy;
    private Genre drama;

    @BeforeEach
    void setUp() {
        comedy = new Genre();
        comedy.setId(1);
        comedy.setName("Комедия");

        drama = new Genre();
        drama.setId(2);
        drama.setName("Драма");
    }

    @Test
    void getAllGenres_ShouldReturnList() throws Exception {
        when(genreService.getAllGenres()).thenReturn(List.of(comedy, drama));

        mockMvc.perform(get("/genres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Комедия"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Драма"))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getGenreById_ShouldReturnGenre() throws Exception {
        when(genreService.getGenreById(1)).thenReturn(comedy);

        mockMvc.perform(get("/genres/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Комедия"));
    }

    @Test
    void getGenreById_WhenNotFound_ShouldReturn404() throws Exception {
        when(genreService.getGenreById(999))
                .thenThrow(new NotFoundException("Жанр с id=999 не найден"));

        mockMvc.perform(get("/genres/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void getAllGenres_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        when(genreService.getAllGenres()).thenReturn(List.of());

        mockMvc.perform(get("/genres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}