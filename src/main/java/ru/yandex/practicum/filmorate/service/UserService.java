package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.NotFoundException;
import ru.yandex.practicum.filmorate.dal.repositories.UserRepository;
import ru.yandex.practicum.filmorate.dto.user.CreateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserResponse;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> getAllUsers() {
        log.debug("Запрос всех пользователей");
        return userRepository.findAll().stream()
                .map(UserMapper::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(int id) {
        log.debug("Поиск пользователя по id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
        return UserMapper.mapToUserResponse(user);
    }

    public UserResponse createUser(CreateUserRequest request) {
        log.info("Создание пользователя: {}", request);

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicatedDataException("Пользователь с email " + request.getEmail() + " уже существует");
        }

        User user = UserMapper.mapToUser(request);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Имя пользователя пустое, установлен логин: {}", user.getLogin());
        }

        User savedUser = userRepository.save(user);
        return UserMapper.mapToUserResponse(savedUser);
    }

    public UserResponse updateUser(UpdateUserRequest request) {
        log.info("Обновление пользователя с id: {}", request.getId());

        if (request.getId() == null) {
            throw new NotFoundException("ID пользователя должен быть указан");
        }

        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + request.getId() + " не найден"));

        if (request.hasEmail() && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new DuplicatedDataException("Пользователь с email " + request.getEmail() + " уже существует");
            }
        }

        User updatedUser = UserMapper.updateUserFields(user, request);

        if (updatedUser.getName() == null || updatedUser.getName().isBlank()) {
            updatedUser.setName(updatedUser.getLogin());
            log.debug("Имя пользователя пустое, установлен логин: {}", updatedUser.getLogin());
        }

        User savedUser = userRepository.update(updatedUser);
        return UserMapper.mapToUserResponse(savedUser);
    }

    public void sendFriendRequest(int userId, int friendId) {
        log.info("Отправка запроса в друзья: {} -> {}", userId, friendId);

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        userRepository.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + friendId + " не найден"));

        userRepository.sendFriendRequest(userId, friendId);
        log.info("Запрос в друзья отправлен от {} к {}", userId, friendId);
    }

    public void acceptFriendRequest(int userId, int friendId) {
        log.info("Подтверждение дружбы: {} подтверждает запрос от {}", userId, friendId);

        userRepository.acceptFriendRequest(userId, friendId);
        log.info("Дружба между {} и {} подтверждена", userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        log.info("Удаление из друзей: пользователь {} удаляет пользователя {}", userId, friendId);

        userRepository.removeFriend(userId, friendId);
        log.info("Пользователи {} и {} больше не друзья", userId, friendId);
    }

    public List<UserResponse> getFriends(int userId) {
        log.info("Получение списка друзей пользователя {}", userId);

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        return userRepository.getFriends(userId).stream()
                .map(UserMapper::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getPendingRequests(int userId) {
        log.info("Получение списка входящих запросов в друзья для {}", userId);

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        return userRepository.getPendingRequests(userId).stream()
                .map(UserMapper::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getCommonFriends(int userId, int otherUserId) {
        log.info("Получение общих друзей пользователей {} и {}", userId, otherUserId);

        return userRepository.getCommonFriends(userId, otherUserId).stream()
                .map(UserMapper::mapToUserResponse)
                .collect(Collectors.toList());
    }
}