package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.controller.NotFoundException;
import ru.yandex.practicum.filmorate.dal.repositories.MpaRepository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MpaServiceTest {

    @Mock
    private MpaRepository mpaRepository;

    @InjectMocks
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
    void getAllMpa_ShouldReturnAllRatings() {
        List<Mpa> expectedMpa = List.of(g, pg, pg13, r, nc17);
        when(mpaRepository.findAll()).thenReturn(expectedMpa);

        List<Mpa> result = mpaService.getAllMpa();

        assertEquals(5, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals("G", result.get(0).getName());
        assertEquals(2, result.get(1).getId());
        assertEquals("PG", result.get(1).getName());
        assertEquals(3, result.get(2).getId());
        assertEquals("PG-13", result.get(2).getName());
        assertEquals(4, result.get(3).getId());
        assertEquals("R", result.get(3).getName());
        assertEquals(5, result.get(4).getId());
        assertEquals("NC-17", result.get(4).getName());

        verify(mpaRepository, times(1)).findAll();
    }

    @Test
    void getAllMpa_WhenEmpty_ShouldReturnEmptyList() {
        when(mpaRepository.findAll()).thenReturn(List.of());

        List<Mpa> result = mpaService.getAllMpa();

        assertTrue(result.isEmpty());
        verify(mpaRepository, times(1)).findAll();
    }

    @Test
    void getMpaById_ShouldReturnRating() {
        when(mpaRepository.findById(3)).thenReturn(Optional.of(pg13));

        Mpa result = mpaService.getMpaById(3);

        assertEquals(3, result.getId());
        assertEquals("PG-13", result.getName());
        verify(mpaRepository, times(1)).findById(3);
    }

    @Test
    void getMpaById_WhenNotFound_ShouldThrowException() {
        when(mpaRepository.findById(999)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> mpaService.getMpaById(999));

        assertEquals("Рейтинг MPA с id=999 не найден", exception.getMessage());
        verify(mpaRepository, times(1)).findById(999);
    }

    @Test
    void getMpaById_ShouldReturnCorrectRatingForEachId() {
        when(mpaRepository.findById(1)).thenReturn(Optional.of(g));
        when(mpaRepository.findById(2)).thenReturn(Optional.of(pg));
        when(mpaRepository.findById(3)).thenReturn(Optional.of(pg13));
        when(mpaRepository.findById(4)).thenReturn(Optional.of(r));
        when(mpaRepository.findById(5)).thenReturn(Optional.of(nc17));

        Mpa result1 = mpaService.getMpaById(1);
        assertEquals("G", result1.getName());

        Mpa result2 = mpaService.getMpaById(2);
        assertEquals("PG", result2.getName());

        Mpa result3 = mpaService.getMpaById(3);
        assertEquals("PG-13", result3.getName());

        Mpa result4 = mpaService.getMpaById(4);
        assertEquals("R", result4.getName());

        Mpa result5 = mpaService.getMpaById(5);
        assertEquals("NC-17", result5.getName());

        verify(mpaRepository, times(1)).findById(1);
        verify(mpaRepository, times(1)).findById(2);
        verify(mpaRepository, times(1)).findById(3);
        verify(mpaRepository, times(1)).findById(4);
        verify(mpaRepository, times(1)).findById(5);
    }

    @Test
    void getMpaById_ShouldPreserveOrder() {
        when(mpaRepository.findById(5)).thenReturn(Optional.of(nc17));
        when(mpaRepository.findById(1)).thenReturn(Optional.of(g));
        when(mpaRepository.findById(3)).thenReturn(Optional.of(pg13));

        Mpa result1 = mpaService.getMpaById(5);
        Mpa result2 = mpaService.getMpaById(1);
        Mpa result3 = mpaService.getMpaById(3);

        assertEquals("NC-17", result1.getName());
        assertEquals("G", result2.getName());
        assertEquals("PG-13", result3.getName());
    }
}
