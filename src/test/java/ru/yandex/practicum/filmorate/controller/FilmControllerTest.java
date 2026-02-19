package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FilmController.class)
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FilmService filmService;

    private Film validFilm;
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

        validFilm = new Film();
        validFilm.setName("Valid Film");
        validFilm.setDescription("A valid film description");
        validFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        validFilm.setDuration(120);
        validFilm.setMpa(mpa);
        validFilm.setGenres(genres);
    }

    @Test
    void createValidFilmShouldReturnOk() throws Exception {
        Film createdFilm = new Film();
        createdFilm.setId(1);
        createdFilm.setName("Valid Film");
        createdFilm.setDescription("A valid film description");
        createdFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        createdFilm.setDuration(120);
        createdFilm.setMpa(mpa);
        createdFilm.setGenres(genres);

        when(filmService.createFilm(any(Film.class))).thenReturn(createdFilm);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Valid Film"))
                .andExpect(jsonPath("$.mpa.id").value(1))
                .andExpect(jsonPath("$.genres[0].id").value(1));
    }

    @Test
    void getAllFilmsShouldReturnOk() throws Exception {
        when(filmService.getAllFilms()).thenReturn(List.of(validFilm));

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Valid Film"))
                .andExpect(jsonPath("$[0].mpa.name").value("PG-13"));
    }

    @Test
    void createFilmWithEmptyNameShouldReturnBadRequest() throws Exception {
        validFilm.setName("");

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void createFilmWithNullNameShouldReturnBadRequest() throws Exception {
        validFilm.setName(null);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void createFilmWithBlankNameShouldReturnBadRequest() throws Exception {
        validFilm.setName("   ");

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void createFilmWithTooLongDescriptionShouldReturnBadRequest() throws Exception {
        validFilm.setDescription("A".repeat(201));

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void createFilmWithExactly200CharDescriptionShouldReturnOk() throws Exception {
        validFilm.setDescription("A".repeat(200));
        when(filmService.createFilm(any(Film.class))).thenReturn(validFilm);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isOk());
    }

    @Test
    void createFilmWithOldReleaseDateShouldReturnBadRequest() throws Exception {
        validFilm.setReleaseDate(LocalDate.of(1895, 12, 27));

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void createFilmWithExactMinReleaseDateShouldReturnOk() throws Exception {
        validFilm.setReleaseDate(LocalDate.of(1895, 12, 28));
        when(filmService.createFilm(any(Film.class))).thenReturn(validFilm);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isOk());
    }

    @Test
    void createFilmWithNullReleaseDateShouldReturnBadRequest() throws Exception {
        validFilm.setReleaseDate(null);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void createFilmWithNegativeDurationShouldReturnBadRequest() throws Exception {
        validFilm.setDuration(-10);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void createFilmWithZeroDurationShouldReturnBadRequest() throws Exception {
        validFilm.setDuration(0);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void updateFilmShouldReturnOk() throws Exception {
        Film updatedFilm = new Film();
        updatedFilm.setId(1);
        updatedFilm.setName("Updated Film Name");
        updatedFilm.setDescription("A valid film description");
        updatedFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        updatedFilm.setDuration(120);
        updatedFilm.setMpa(mpa);
        updatedFilm.setGenres(genres);

        when(filmService.updateFilm(any(Film.class))).thenReturn(updatedFilm);

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFilm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Film Name"))
                .andExpect(jsonPath("$.mpa.name").value("PG-13"));
    }

    @Test
    void createFilmWithEmptyBodyShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void updateNonExistentFilmShouldThrowException() throws Exception {
        validFilm.setId(999);
        when(filmService.updateFilm(any(Film.class)))
                .thenThrow(new NotFoundException("Фильм с id=999 не найден"));

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void getFilmById_ShouldReturnFilm() throws Exception {
        Film createdFilm = new Film();
        createdFilm.setId(1);
        createdFilm.setName("Valid Film");
        createdFilm.setDescription("A valid film description");
        createdFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        createdFilm.setDuration(120);
        createdFilm.setMpa(mpa);
        createdFilm.setGenres(genres);

        when(filmService.getFilmById(1)).thenReturn(createdFilm);

        mockMvc.perform(get("/films/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Valid Film"))
                .andExpect(jsonPath("$.mpa.name").value("PG-13"));
    }

    @Test
    void getFilmById_WhenNotFound_ShouldReturn404() throws Exception {
        when(filmService.getFilmById(999))
                .thenThrow(new NotFoundException("Фильм с id=999 не найден"));

        mockMvc.perform(get("/films/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void updateGenres_ShouldReturnOk() throws Exception {
        when(filmService.updateGenres(anyInt(), anySet())).thenReturn(validFilm);

        mockMvc.perform(put("/films/{id}/genres", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(genres)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.genres[0].name").value("Комедия"));
    }

    @Test
    void updateMpa_ShouldReturnOk() throws Exception {
        when(filmService.updateMpa(anyInt(), any(Mpa.class))).thenReturn(validFilm);

        mockMvc.perform(put("/films/{id}/mpa", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mpa)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mpa.name").value("PG-13"));
    }

    @Test
    void addLike_ShouldReturnOk() throws Exception {
        doNothing().when(filmService).addLike(1, 1);

        mockMvc.perform(put("/films/{id}/like/{userId}", 1, 1))
                .andExpect(status().isOk());
    }

    @Test
    void removeLike_ShouldReturnOk() throws Exception {
        doNothing().when(filmService).removeLike(1, 1);

        mockMvc.perform(delete("/films/{id}/like/{userId}", 1, 1))
                .andExpect(status().isOk());
    }

    @Test
    void getPopular_ShouldReturnList() throws Exception {
        when(filmService.getPopular(10)).thenReturn(List.of(validFilm));

        mockMvc.perform(get("/films/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getPopular_WithCountParameter_ShouldReturnLimitedList() throws Exception {
        when(filmService.getPopular(5)).thenReturn(List.of(validFilm));

        mockMvc.perform(get("/films/popular?count=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}