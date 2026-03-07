package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MpaController.class)
class MpaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MpaService mpaService;

    private Mpa g;
    private Mpa pg;
    private Mpa pg13;
    private Mpa r;
    private Mpa nc17;

    @BeforeEach
    void setUp() {
        g = new Mpa();
        g.setId(1);
        g.setName("G");

        pg = new Mpa();
        pg.setId(2);
        pg.setName("PG");

        pg13 = new Mpa();
        pg13.setId(3);
        pg13.setName("PG-13");

        r = new Mpa();
        r.setId(4);
        r.setName("R");

        nc17 = new Mpa();
        nc17.setId(5);
        nc17.setName("NC-17");
    }

    @Test
    void getAllMpa_ShouldReturnAllRatings() throws Exception {
        when(mpaService.getAllMpa()).thenReturn(List.of(g, pg, pg13, r, nc17));

        mockMvc.perform(get("/mpa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("G"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("PG"))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].name").value("PG-13"))
                .andExpect(jsonPath("$[3].id").value(4))
                .andExpect(jsonPath("$[3].name").value("R"))
                .andExpect(jsonPath("$[4].id").value(5))
                .andExpect(jsonPath("$[4].name").value("NC-17"))
                .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    void getMpaById_ShouldReturnRating() throws Exception {
        when(mpaService.getMpaById(3)).thenReturn(pg13);

        mockMvc.perform(get("/mpa/{id}", 3))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("PG-13"));
    }

    @Test
    void getMpaById_WhenNotFound_ShouldReturn404() throws Exception {
        when(mpaService.getMpaById(999))
                .thenThrow(new NotFoundException("Рейтинг MPA с id=999 не найден"));

        mockMvc.perform(get("/mpa/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void getAllMpa_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        when(mpaService.getAllMpa()).thenReturn(List.of());

        mockMvc.perform(get("/mpa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getMpaById_ShouldReturnCorrectRatingForEachId() throws Exception {
        when(mpaService.getMpaById(1)).thenReturn(g);
        when(mpaService.getMpaById(2)).thenReturn(pg);
        when(mpaService.getMpaById(3)).thenReturn(pg13);
        when(mpaService.getMpaById(4)).thenReturn(r);
        when(mpaService.getMpaById(5)).thenReturn(nc17);

        mockMvc.perform(get("/mpa/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("G"));

        mockMvc.perform(get("/mpa/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("PG"));

        mockMvc.perform(get("/mpa/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("PG-13"));

        mockMvc.perform(get("/mpa/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("R"));

        mockMvc.perform(get("/mpa/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NC-17"));
    }
}