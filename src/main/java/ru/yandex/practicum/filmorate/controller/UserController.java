package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> findAll() {
        log.info("Получен запрос на получение всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        log.info("GET /users/{}", id);
        return userService.getUserById(id);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Получен запрос на создание пользователя: {}", user);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Имя пользователя пустое, установлен логин: {}", user.getLogin());
        }

        return userService.createUser(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Получен запрос на обновление пользователя с id: {}", user.getId());

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Имя пользователя пустое, установлен логин: {}", user.getLogin());
        }

        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void sendFriendRequest(@PathVariable int id, @PathVariable int friendId) {
        log.info("PUT /users/{}/friends/{} - отправка запроса в друзья", id, friendId);
        userService.sendFriendRequest(id, friendId);
    }

    @PutMapping("/{id}/friends/{friendId}/accept")
    public void acceptFriendRequest(@PathVariable int id, @PathVariable int friendId) {
        log.info("PUT /users/{}/friends/{}/accept - подтверждение дружбы", id, friendId);
        userService.acceptFriendRequest(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("DELETE /users/{}/friends/{} - удаление из друзей", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        log.info("GET /users/{}/friends - получение списка друзей", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/pending")
    public List<User> getPendingRequests(@PathVariable int id) {
        log.info("GET /users/{}/friends/pending - получение входящих запросов", id);
        return userService.getPendingRequests(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("GET /users/{}/friends/common/{} - получение общих друзей", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}