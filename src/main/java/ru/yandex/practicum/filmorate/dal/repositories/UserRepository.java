package ru.yandex.practicum.filmorate.dal.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository extends BaseRepository<User> {

    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = ?";
    private static final String INSERT_QUERY = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM users WHERE user_id = ?";

    private static final String INSERT_FRIEND_QUERY = "INSERT INTO friendship (user_id, friend_id, status_id) VALUES (?, ?, ?)";
    private static final String DELETE_FRIEND_QUERY = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ? AND status_id = ?";
    private static final String FIND_FRIENDS_QUERY =
            "SELECT u.* FROM users u " +
                    "JOIN friendship f ON u.user_id = f.friend_id " +
                    "WHERE f.user_id = ? AND f.status_id = ?";
    private static final String FIND_COMMON_FRIENDS_QUERY =
            "SELECT u.* FROM users u " +
                    "JOIN friendship f1 ON u.user_id = f1.friend_id AND f1.user_id = ? AND f1.status_id = ? " +
                    "JOIN friendship f2 ON u.user_id = f2.friend_id AND f2.user_id = ? AND f2.status_id = ?";

    private static final int FRIEND_STATUS = 1;

    public UserRepository(JdbcTemplate jdbc, UserRowMapper mapper) {
        super(jdbc, mapper);
    }

    public List<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<User> findById(long userId) {
        return findOne(FIND_BY_ID_QUERY, userId);
    }

    public Optional<User> findByEmail(String email) {
        return findOne(FIND_BY_EMAIL_QUERY, email);
    }

    public User save(User user) {
        long id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                java.sql.Date.valueOf(user.getBirthday())
        );
        user.setId((int) id);
        return user;
    }

    public User update(User user) {
        update(
                UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                java.sql.Date.valueOf(user.getBirthday()),
                user.getId()
        );
        return user;
    }

    public boolean delete(long userId) {
        return delete(DELETE_QUERY, userId);
    }

    public void addFriend(int userId, int friendId) {
        jdbc.update(INSERT_FRIEND_QUERY, userId, friendId, FRIEND_STATUS);
    }

    public void removeFriend(int userId, int friendId) {
        jdbc.update(DELETE_FRIEND_QUERY, userId, friendId, FRIEND_STATUS);
    }

    public List<User> getFriends(int userId) {
        return jdbc.query(FIND_FRIENDS_QUERY, mapper, userId, FRIEND_STATUS);
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        return jdbc.query(FIND_COMMON_FRIENDS_QUERY, mapper,
                userId, FRIEND_STATUS, otherUserId, FRIEND_STATUS);
    }
}