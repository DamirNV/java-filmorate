package ru.yandex.practicum.filmorate.dal.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Repository
public class FilmRepository extends BaseRepository<Film> {

    private static final String FIND_ALL_QUERY =
            "SELECT f.*, m.code as mpa_name FROM films f LEFT JOIN mpa_rating m ON f.mpa_rating_id = m.mpa_rating_id";
    private static final String FIND_BY_ID_QUERY =
            "SELECT f.*, m.code as mpa_name FROM films f LEFT JOIN mpa_rating m ON f.mpa_rating_id = m.mpa_rating_id WHERE f.film_id = ?";
    private static final String INSERT_QUERY =
            "INSERT INTO films (title, description, release_date, duration, mpa_rating_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY =
            "UPDATE films SET title = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ? WHERE film_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM films WHERE film_id = ?";

    private static final String INSERT_GENRE_QUERY = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_GENRES_QUERY = "DELETE FROM film_genres WHERE film_id = ?";
    private static final String FIND_GENRES_QUERY =
            "SELECT g.* FROM genres g JOIN film_genres fg ON g.genre_id = fg.genre_id WHERE fg.film_id = ? ORDER BY g.genre_id";

    private static final String INSERT_LIKE_QUERY = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String FIND_LIKES_QUERY = "SELECT user_id FROM likes WHERE film_id = ?";

    private final JdbcTemplate jdbc;

    public FilmRepository(JdbcTemplate jdbc, FilmRowMapper mapper) {
        super(jdbc, mapper);
        this.jdbc = jdbc;
    }

    @Override
    public List<Film> findMany(String query, Object... params) {
        List<Film> films = super.findMany(query, params);
        films.forEach(this::loadGenres);
        films.forEach(this::loadLikes);
        return films;
    }

    @Override
    public Optional<Film> findOne(String query, Object... params) {
        Optional<Film> film = super.findOne(query, params);
        film.ifPresent(this::loadGenres);
        film.ifPresent(this::loadLikes);
        return film;
    }

    public List<Film> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Film> findById(long filmId) {
        return findOne(FIND_BY_ID_QUERY, filmId);
    }

    public Film save(Film film) {
        long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId((int) id);

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            saveGenres(film.getId(), film.getGenres());
        }

        loadGenres(film);
        loadLikes(film);

        return film;
    }

    public Film update(Film film) {
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        jdbc.update(DELETE_GENRES_QUERY, film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            saveGenres(film.getId(), film.getGenres());
        }

        loadGenres(film);
        loadLikes(film);

        return film;
    }

    public boolean delete(long filmId) {
        return delete(DELETE_QUERY, filmId);
    }

    private void saveGenres(int filmId, Set<Genre> genres) {
        for (Genre genre : genres) {
            jdbc.update(INSERT_GENRE_QUERY, filmId, genre.getId());
        }
    }

    private void loadGenres(Film film) {
        String sql = "SELECT g.* FROM genres g JOIN film_genres fg ON g.genre_id = fg.genre_id WHERE fg.film_id = ? ORDER BY g.genre_id";
        List<Genre> genres = jdbc.query(sql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, film.getId());
        film.setGenres(new LinkedHashSet<>(genres));
    }

    private void loadLikes(Film film) {
        List<Integer> likes = jdbc.query(FIND_LIKES_QUERY, (rs, rowNum) -> rs.getInt("user_id"), film.getId());
        film.setLikes(new HashSet<>(likes));
    }

    public void addLike(int filmId, int userId) {
        jdbc.update(INSERT_LIKE_QUERY, filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        jdbc.update(DELETE_LIKE_QUERY, filmId, userId);
    }
}
