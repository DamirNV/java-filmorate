package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.dto.film.CreateFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.FilmResponse;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
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

    private CreateFilmRequest createRequest;
    private FilmResponse filmResponse;
    private UpdateFilmRequest updateRequest;
    private Mpa mpa;
    private CreateFilmRequest.GenreId genreId1;
    private CreateFilmRequest.GenreId genreId2;
    private Set<CreateFilmRequest.GenreId> genreIds;
    private Set<Genre> responseGenres;

    @BeforeEach
    void setUp() {
        mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("PG-13");

        genreId1 = new CreateFilmRequest.GenreId();
        genreId1.setId(1);

        genreId2 = new CreateFilmRequest.GenreId();
        genreId2.setId(2);

        genreIds = new LinkedHashSet<>();
        genreIds.add(genreId1);
        genreIds.add(genreId2);

        Genre responseGenre1 = new Genre();
        responseGenre1.setId(1);
        responseGenre1.setName("Комедия");

        Genre responseGenre2 = new Genre();
        responseGenre2.setId(2);
        responseGenre2.setName("Драма");

        responseGenres = new LinkedHashSet<>();
        responseGenres.add(responseGenre1);
        responseGenres.add(responseGenre2);

        createRequest = new CreateFilmRequest();
        createRequest.setName("Valid Film");
        createRequest.setDescription("A valid film description");
        createRequest.setReleaseDate(LocalDate.of(2000, 1, 1));
        createRequest.setDuration(120);
        createRequest.setMpa(mpa);
        createRequest.setGenres(genreIds);

        filmResponse = new FilmResponse();
        filmResponse.setId(1L);
        filmResponse.setName("Valid Film");
        filmResponse.setDescription("A valid film description");
        filmResponse.setReleaseDate(LocalDate.of(2000, 1, 1));
        filmResponse.setDuration(120);
        filmResponse.setMpa(mpa);
        filmResponse.setGenres(responseGenres);
        filmResponse.setLikes(Set.of());

        updateRequest = new UpdateFilmRequest();
        updateRequest.setId(1L);
        updateRequest.setName("Updated Film");
        updateRequest.setDescription("Updated description");
        updateRequest.setReleaseDate(LocalDate.of(2000, 1, 1));
        updateRequest.setDuration(120);
        updateRequest.setMpa(mpa);
        updateRequest.setGenres(genreIds);
    }

    @Test
    void createFilm_ShouldReturnCreatedFilm() throws Exception {
        when(filmService.createFilm(any(CreateFilmRequest.class))).thenReturn(filmResponse);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Valid Film"))
                .andExpect(jsonPath("$.mpa.id").value(1))
                .andExpect(jsonPath("$.mpa.name").value("PG-13"))
                .andExpect(jsonPath("$.genres[0].id").value(1))
                .andExpect(jsonPath("$.genres[0].name").value("Комедия"))
                .andExpect(jsonPath("$.genres[1].id").value(2))
                .andExpect(jsonPath("$.genres[1].name").value("Драма"));
    }

    @Test
    void getAllFilms_ShouldReturnList() throws Exception {
        when(filmService.getAllFilms()).thenReturn(List.of(filmResponse));

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Valid Film"))
                .andExpect(jsonPath("$[0].mpa.name").value("PG-13"));
    }

    @Test
    void getFilmById_ShouldReturnFilm() throws Exception {
        when(filmService.getFilmById(1)).thenReturn(filmResponse);

        mockMvc.perform(get("/films/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Valid Film"));
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
    void updateFilm_ShouldReturnUpdatedFilm() throws Exception {
        FilmResponse updatedResponse = new FilmResponse();
        updatedResponse.setId(1L);
        updatedResponse.setName("Updated Film");
        updatedResponse.setDescription("Updated description");
        updatedResponse.setReleaseDate(LocalDate.of(2000, 1, 1));
        updatedResponse.setDuration(120);
        updatedResponse.setMpa(mpa);
        updatedResponse.setGenres(responseGenres);

        when(filmService.updateFilm(any(UpdateFilmRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Film"));
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
        when(filmService.getPopular(10)).thenReturn(List.of(filmResponse));

        mockMvc.perform(get("/films/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Valid Film"));
    }

    @Test
    void getPopular_WithCountParameter_ShouldRespectCount() throws Exception {
        when(filmService.getPopular(5)).thenReturn(List.of(filmResponse));

        mockMvc.perform(get("/films/popular?count=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Valid Film"));
    }

    @Test
    void createFilm_WithInvalidData_ShouldReturn400() throws Exception {
        createRequest.setName("");

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
    }
}