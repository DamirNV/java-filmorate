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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Slf4j
@Primary
@RequiredArgsConstructor
@Repository("userDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    };

    @Override
    public User add(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            user.setId(key.intValue());
        }

        log.info("Пользователь добавлен в БД с id: {}", user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        int updated = jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId());

        if (updated == 0) {
            throw new NotFoundException("Пользователь с id=" + user.getId() + " не найден");
        }

        log.info("Пользователь с id {} обновлен в БД", user.getId());
        return user;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        int deleted = jdbcTemplate.update(sql, id);
        if (deleted > 0) {
            log.info("Пользователь с id {} удален из БД", id);
            return true;
        }
        return false;
    }

    @Override
    public List<User> getAll() {
        String sql = "SELECT * FROM users";
        log.debug("Запрос всех пользователей из БД");
        return jdbcTemplate.query(sql, userRowMapper);
    }

    @Override
    public Optional<User> getById(int id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, id);
        return users.stream().findFirst();
    }

    public void sendFriendRequest(int userId, int friendId) {
        String sql = "INSERT INTO friendship (user_id, friend_id, status_id) VALUES (?, ?, 1)";
        jdbcTemplate.update(sql, userId, friendId);
        log.info("Запрос в друзья отправлен: {} -> {}", userId, friendId);
    }

    public void acceptFriendRequest(int userId, int friendId) {
        String sql = "UPDATE friendship SET status_id = 2 WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, friendId, userId);
        log.info("Запрос в друзья подтвержден: {} принял запрос от {}", userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        String sql = "DELETE FROM friendship WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";
        jdbcTemplate.update(sql, userId, friendId, friendId, userId);
        log.info("Дружба удалена между {} и {}", userId, friendId);
    }

    public List<User> getFriends(int userId) {
        String sql = "SELECT u.* FROM users u " +
                "JOIN friendship f ON u.user_id = f.friend_id " +
                "WHERE f.user_id = ? AND f.status_id = 2";
        return jdbcTemplate.query(sql, userRowMapper, userId);
    }

    public List<User> getPendingRequests(int userId) {
        String sql = "SELECT u.* FROM users u " +
                "JOIN friendship f ON u.user_id = f.user_id " +
                "WHERE f.friend_id = ? AND f.status_id = 1";
        return jdbcTemplate.query(sql, userRowMapper, userId);
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        String sql = "SELECT u.* FROM users u " +
                "JOIN friendship f1 ON u.user_id = f1.friend_id AND f1.user_id = ? AND f1.status_id = 2 " +
                "JOIN friendship f2 ON u.user_id = f2.friend_id AND f2.user_id = ? AND f2.status_id = 2";
        return jdbcTemplate.query(sql, userRowMapper, userId, otherUserId);
    }
}
