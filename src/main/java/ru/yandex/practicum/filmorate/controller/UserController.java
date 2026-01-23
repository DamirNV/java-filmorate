package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int idCounter = 1;

    @GetMapping
    public List<User> findAll() {
        log.info("Получен запрос на получение всех пользователей. Текущее количество: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Получен запрос на создание пользователя: {}", user);
        user.setId(idCounter++);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Имя пользователя пустое, установлен логин: {}", user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Пользователь успешно создан с id: {}", user.getId());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Получен запрос на обновление пользователя с id: {}", user.getId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Имя пользователя пустое, установлен логин: {}", user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Пользователь с id {} успешно обновлен", user.getId());
        return user;
    }
}