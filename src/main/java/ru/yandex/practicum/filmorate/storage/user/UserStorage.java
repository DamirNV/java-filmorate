package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User add(User user);

    User update(User user);

    boolean delete(int id);

    List<User> getAll();

    Optional<User> getById(int id);

    void sendFriendRequest(int userId, int friendId);

    void acceptFriendRequest(int userId, int friendId);

    void removeFriend(int userId, int friendId);

    List<User> getFriends(int userId);

    List<User> getPendingRequests(int userId);

    List<User> getCommonFriends(int userId, int otherUserId);

}
