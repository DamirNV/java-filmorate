package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.user.CreateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserResponse;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserResponse> findAll() {
        log.info("Получен запрос на получение всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable int id) {
        log.info("GET /users/{}", id);
        return userService.getUserById(id);
    }

    @PostMapping
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        log.info("Получен запрос на создание пользователя: {}", request);
        return userService.createUser(request);
    }

    @PutMapping
    public UserResponse update(@Valid @RequestBody UpdateUserRequest request) {
        log.info("Получен запрос на обновление пользователя с id: {}", request.getId());
        return userService.updateUser(request);
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
    public List<UserResponse> getFriends(@PathVariable int id) {
        log.info("GET /users/{}/friends - получение списка друзей", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/pending")
    public List<UserResponse> getPendingRequests(@PathVariable int id) {
        log.info("GET /users/{}/friends/pending - получение входящих запросов", id);
        return userService.getPendingRequests(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserResponse> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("GET /users/{}/friends/common/{} - получение общих друзей", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}