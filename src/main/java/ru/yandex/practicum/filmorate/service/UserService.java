package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    public void sendFriendRequest(int userId, int friendId) {
        log.info("Отправка запроса в друзья: {} -> {}", userId, friendId);

        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        String sql = "INSERT INTO friendship (user_id, friend_id, status_id) VALUES (?, ?, 1)";
        // TODO: реализовать через JdbcTemplate

        log.info("Запрос в друзья отправлен от {} к {}", userId, friendId);
    }

    public void acceptFriendRequest(int userId, int friendId) {
        log.info("Подтверждение дружбы: {} подтверждает запрос от {}", userId, friendId);

        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        String sql = "UPDATE friendship SET status_id = 2 WHERE user_id = ? AND friend_id = ?";
        // TODO: реализовать через JdbcTemplate

        log.info("Дружба между {} и {} подтверждена", userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        log.info("Удаление из друзей: пользователь {} удаляет пользователя {}", userId, friendId);

        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        String sql = "DELETE FROM friendship WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";
        // TODO: реализовать через JdbcTemplate

        log.info("Пользователи {} и {} больше не друзья", userId, friendId);
    }

    public List<User> getFriends(int userId) {
        log.info("Получение списка друзей пользователя {}", userId);

        User user = getUserOrThrow(userId);

        String sql = "SELECT u.* FROM users u JOIN friendship f ON u.user_id = f.friend_id WHERE f.user_id = ? AND f.status_id = 2";
        // TODO: реализовать через JdbcTemplate
        return List.of(); // временно
    }

    public List<User> getPendingRequests(int userId) {
        log.info("Получение списка входящих запросов в друзья для {}", userId);

        User user = getUserOrThrow(userId);

        String sql = "SELECT u.* FROM users u JOIN friendship f ON u.user_id = f.user_id WHERE f.friend_id = ? AND f.status_id = 1";
        // TODO: реализовать через JdbcTemplate
        return List.of(); // временно
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        log.info("Получение общих друзей пользователей {} и {}", userId, otherUserId);

        User user = getUserOrThrow(userId);
        User otherUser = getUserOrThrow(otherUserId);

        String sql = "SELECT u.* FROM users u " +
                "JOIN friendship f1 ON u.user_id = f1.friend_id AND f1.user_id = ? AND f1.status_id = 2 " +
                "JOIN friendship f2 ON u.user_id = f2.friend_id AND f2.user_id = ? AND f2.status_id = 2";
        // TODO: реализовать через JdbcTemplate
        return List.of(); // временно
    }

    private User getUserOrThrow(int id) {
        return userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
    }

    public List<User> getAllUsers() {
        log.debug("Запрос всех пользователей");
        return userStorage.getAll();
    }

    public User getUserById(int id) {
        log.debug("Поиск пользователя по id: {}", id);
        return userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
    }

    public User createUser(User user) {
        log.info("Создание пользователя: {}", user);
        return userStorage.add(user);
    }

    public User updateUser(User user) {
        log.info("Обновление пользователя с id: {}", user.getId());
        return userStorage.update(user);
    }
}