package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.controller.NotFoundException;

import java.util.*;

@Slf4j
@Component
@Repository("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int idCounter = 1;

    @Override
    public User add(User user) {
        user.setId(idCounter++);
        users.put(user.getId(), user);
        log.info("Пользователь добавлен в хранилище с id: {}", user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь с id=" + user.getId() + " не найден");
        }
        users.put(user.getId(), user);
        log.info("Пользователь с id {} обновлен в хранилище", user.getId());
        return user;
    }

    @Override
    public boolean delete(int id) {
        User removed = users.remove(id);
        if (removed != null) {
            log.info("Пользователь с id {} удален из хранилища", id);
            return true;
        }
        return false;
    }

    @Override
    public List<User> getAll() {
        log.debug("Запрос всех пользователей. Количество: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getById(int id) {
        log.debug("Поиск пользователя по id: {}", id);
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void sendFriendRequest(int userId, int friendId) {
        log.debug("sendFriendRequest вызван для InMemoryUserStorage, операция не поддерживается (работает только БД)");
    }

    @Override
    public void acceptFriendRequest(int userId, int friendId) {
        log.debug("acceptFriendRequest вызван для InMemoryUserStorage, операция не поддерживается (работает только БД)");
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        log.debug("removeFriend вызван для InMemoryUserStorage, операция не поддерживается (работает только БД)");
    }

    @Override
    public List<User> getFriends(int userId) {
        log.debug("getFriends вызван для InMemoryUserStorage, возвращаем пустой список");
        return List.of();
    }

    @Override
    public List<User> getPendingRequests(int userId) {
        log.debug("getPendingRequests вызван для InMemoryUserStorage, возвращаем пустой список");
        return List.of();
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherUserId) {
        log.debug("getCommonFriends вызван для InMemoryUserStorage, возвращаем пустой список");
        return List.of();
    }

}
