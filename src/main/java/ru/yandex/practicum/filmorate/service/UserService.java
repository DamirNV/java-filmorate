package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final UserStorage userStorage;

    public void sendFriendRequest(int userId, int friendId) {
        log.info("Отправка запроса в друзья: {} -> {}", userId, friendId);

        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        Friendship request = new Friendship();
        request.setUserId(userId);
        request.setFriendId(friendId);
        request.setStatus(FriendshipStatus.PENDING);

        user.getFriendships().add(request);

        log.info("Запрос в друзья отправлен от {} к {}", userId, friendId);
    }

    public void acceptFriendRequest(int userId, int friendId) {
        log.info("Подтверждение дружбы: {} подтверждает запрос от {}", userId, friendId);

        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        Friendship request = user.getFriendships().stream()
                .filter(f -> f.getUserId() == friendId && f.getFriendId() == userId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Запрос в друзья не найден"));

        request.setStatus(FriendshipStatus.CONFIRMED);

        Friendship confirmed = new Friendship();
        confirmed.setUserId(userId);
        confirmed.setFriendId(friendId);
        confirmed.setStatus(FriendshipStatus.CONFIRMED);

        friend.getFriendships().add(confirmed);

        log.info("Дружба между {} и {} подтверждена", userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        log.info("Удаление из друзей: пользователь {} удаляет пользователя {}", userId, friendId);

        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        user.getFriendships().removeIf(f ->
                (f.getUserId() == userId && f.getFriendId() == friendId) ||
                        (f.getUserId() == friendId && f.getFriendId() == userId)
        );

        friend.getFriendships().removeIf(f ->
                (f.getUserId() == userId && f.getFriendId() == friendId) ||
                        (f.getUserId() == friendId && f.getFriendId() == userId)
        );

        log.info("Пользователи {} и {} больше не друзья", userId, friendId);
    }

    public List<User> getFriends(int userId) {
        log.info("Получение списка друзей пользователя {}", userId);

        User user = getUserOrThrow(userId);

        return user.getFriendships().stream()
                .filter(f -> f.getStatus() == FriendshipStatus.CONFIRMED)
                .map(f -> {
                    int friendId = f.getUserId() == userId ? f.getFriendId() : f.getUserId();
                    return getUserOrThrow(friendId);
                })
                .collect(Collectors.toList());
    }

    public List<User> getPendingRequests(int userId) {
        log.info("Получение списка входящих запросов в друзья для {}", userId);

        User user = getUserOrThrow(userId);

        return user.getFriendships().stream()
                .filter(f -> f.getFriendId() == userId && f.getStatus() == FriendshipStatus.PENDING)
                .map(f -> getUserOrThrow(f.getUserId()))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        log.info("Получение общих друзей пользователей {} и {}", userId, otherUserId);

        User user = getUserOrThrow(userId);
        User otherUser = getUserOrThrow(otherUserId);

        Set<Integer> userFriendIds = user.getFriendships().stream()
                .filter(f -> f.getStatus() == FriendshipStatus.CONFIRMED)
                .map(f -> f.getUserId() == userId ? f.getFriendId() : f.getUserId())
                .collect(Collectors.toSet());

        Set<Integer> otherUserFriendIds = otherUser.getFriendships().stream()
                .filter(f -> f.getStatus() == FriendshipStatus.CONFIRMED)
                .map(f -> f.getUserId() == otherUserId ? f.getFriendId() : f.getUserId())
                .collect(Collectors.toSet());

        Set<Integer> commonFriendIds = userFriendIds.stream()
                .filter(otherUserFriendIds::contains)
                .collect(Collectors.toSet());

        return commonFriendIds.stream()
                .map(this::getUserOrThrow)
                .collect(Collectors.toList());
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