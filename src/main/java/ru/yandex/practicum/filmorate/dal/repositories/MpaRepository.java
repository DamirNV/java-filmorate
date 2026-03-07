package ru.yandex.practicum.filmorate.dal.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.controller.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Repository
public class MpaRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Mpa> mapper;

    public MpaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = (rs, rowNum) -> {
            Mpa mpa = new Mpa();
            mpa.setId(rs.getInt("mpa_rating_id"));
            mpa.setName(rs.getString("code"));
            return mpa;
        };
    }

    public List<Mpa> findAll() {
        String sql = "SELECT * FROM mpa_rating ORDER BY mpa_rating_id";
        return jdbcTemplate.query(sql, mapper);
    }

    public Mpa findById(int id) {
        String sql = "SELECT * FROM mpa_rating WHERE mpa_rating_id = ?";
        List<Mpa> mpaList = jdbcTemplate.query(sql, mapper, id);
        if (mpaList.isEmpty()) {
            throw new NotFoundException("Рейтинг MPA с id=" + id + " не найден");
        }
        return mpaList.get(0);
    }
}
