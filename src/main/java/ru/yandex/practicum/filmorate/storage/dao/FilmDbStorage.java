package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.controller.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    private RowMapper<Film> filmRowMapper = (rs, rowNum) -> {
        Film film = new Film();
        film.setId(rs.getInt("film_id"));
        film.setName(rs.getString("title"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("mpa_rating_id"));
        mpa.setName(rs.getString("mpa_name"));
        film.setMpa(mpa);

        return film;
    };

    @Override
    public Film add(Film film) {
        String sql = "INSERT INTO films (title, description, release_date, duration, mpa_rating_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            film.setId(key.intValue());
        }

        insertGenres(film.getId(), film.getGenres());
        log.info("Фильм добавлен в БД с id: {}", film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET title = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ? WHERE film_id = ?";
        int updated = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        if (updated == 0) {
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден");
        }

        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
        insertGenres(film.getId(), film.getGenres());

        log.info("Фильм с id {} обновлен в БД", film.getId());
        return film;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM films WHERE film_id = ?";
        int deleted = jdbcTemplate.update(sql, id);
        if (deleted > 0) {
            log.info("Фильм с id {} удален из БД", id);
            return true;
        }
        return false;
    }

    @Override
    public List<Film> getAll() {
        String sql = "SELECT f.*, m.name as mpa_name FROM films f LEFT JOIN mpa_rating m ON f.mpa_rating_id = m.mpa_rating_id";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper);
        films.forEach(this::loadGenres);
        log.debug("Запрос всех фильмов из БД, найдено: {}", films.size());
        return films;
    }

    @Override
    public Optional<Film> getById(int id) {
        String sql = "SELECT f.*, m.name as mpa_name FROM films f LEFT JOIN mpa_rating m ON f.mpa_rating_id = m.mpa_rating_id WHERE f.film_id = ?";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, id);
        films.forEach(this::loadGenres);
        return films.stream().findFirst();
    }

    private void insertGenres(int filmId, Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        for (Genre genre : genres) {
            jdbcTemplate.update(sql, filmId, genre.getId());
        }
    }

    private void loadGenres(Film film) {
        String sql = "SELECT g.* FROM genres g JOIN film_genres fg ON g.genre_id = fg.genre_id WHERE fg.film_id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, film.getId());
        film.setGenres(new LinkedHashSet<>(genres));
    }
}
