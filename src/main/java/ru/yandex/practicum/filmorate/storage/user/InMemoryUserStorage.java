package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.controller.NotFoundException;

import java.util.*;

@Slf4j
@Component
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
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getById(int id) {
        return Optional.ofNullable(users.get(id));
    }
}
