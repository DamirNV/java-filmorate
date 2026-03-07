package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Mpa> mpaRowMapper = (rs, rowNum) -> {
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("mpa_rating_id"));
        mpa.setName(rs.getString("code"));
        return mpa;
    };

    @GetMapping
    public List<Mpa> getAllMpa() {
        log.info("GET /mpa - запрос всех рейтингов");
        String sql = "SELECT * FROM mpa_rating ORDER BY mpa_rating_id";
        return jdbcTemplate.query(sql, mpaRowMapper);
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable int id) {
        log.info("GET /mpa/{} - запрос рейтинга по id", id);
        String sql = "SELECT * FROM mpa_rating WHERE mpa_rating_id = ?";
        List<Mpa> mpaList = jdbcTemplate.query(sql, mpaRowMapper, id);
        if (mpaList.isEmpty()) {
            throw new NotFoundException("Рейтинг MPA с id=" + id + " не найден");
        }
        return mpaList.get(0);
    }
}
