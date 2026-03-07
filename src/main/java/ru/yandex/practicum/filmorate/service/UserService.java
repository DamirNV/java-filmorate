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

        getUserOrThrow(userId);
        getUserOrThrow(friendId);

        userStorage.sendFriendRequest(userId, friendId);  // ← новый метод
        log.info("Запрос в друзья отправлен от {} к {}", userId, friendId);
    }

    public void acceptFriendRequest(int userId, int friendId) {
        log.info("Подтверждение дружбы: {} подтверждает запрос от {}", userId, friendId);

        getUserOrThrow(userId);
        getUserOrThrow(friendId);

        userStorage.acceptFriendRequest(userId, friendId);  // ← новый метод
        log.info("Дружба между {} и {} подтверждена", userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        log.info("Удаление из друзей: пользователь {} удаляет пользователя {}", userId, friendId);

        getUserOrThrow(userId);
        getUserOrThrow(friendId);

        userStorage.removeFriend(userId, friendId);  // ← новый метод
        log.info("Пользователи {} и {} больше не друзья", userId, friendId);
    }

    public List<User> getFriends(int userId) {
        log.info("Получение списка друзей пользователя {}", userId);
        getUserOrThrow(userId);
        return userStorage.getFriends(userId);  // ← новый метод
    }

    public List<User> getPendingRequests(int userId) {
        log.info("Получение списка входящих запросов в друзья для {}", userId);
        getUserOrThrow(userId);
        return userStorage.getPendingRequests(userId);  // ← новый метод
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        log.info("Получение общих друзей пользователей {} и {}", userId, otherUserId);
        getUserOrThrow(userId);
        getUserOrThrow(otherUserId);
        return userStorage.getCommonFriends(userId, otherUserId);  // ← новый метод
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