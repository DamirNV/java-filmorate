package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Genre> genreRowMapper = (rs, rowNum) -> {
        Genre genre = new Genre();
        genre.setId(rs.getInt("genre_id"));
        genre.setName(rs.getString("name"));
        return genre;
    };

    @GetMapping
    public List<Genre> getAllGenres() {
        log.info("GET /genres - запрос всех жанров");
        String sql = "SELECT * FROM genres ORDER BY genre_id";
        return jdbcTemplate.query(sql, genreRowMapper);
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable int id) {
        log.info("GET /genres/{} - запрос жанра по id", id);
        String sql = "SELECT * FROM genres WHERE genre_id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, genreRowMapper, id);
        if (genres.isEmpty()) {
            throw new NotFoundException("Жанр с id=" + id + " не найден");
        }
        return genres.get(0);
    }
}
