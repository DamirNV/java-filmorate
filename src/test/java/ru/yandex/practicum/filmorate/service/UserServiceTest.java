package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.controller.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private UserService userService;

    private User user1;
    private User user2;
    private User user3;
    private Friendship friendship1;
    private Friendship friendship2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1);
        user1.setEmail("user1@test.com");
        user1.setLogin("user1");
        user1.setName("User One");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        user1.setFriendships(new HashSet<>());

        user2 = new User();
        user2.setId(2);
        user2.setEmail("user2@test.com");
        user2.setLogin("user2");
        user2.setName("User Two");
        user2.setBirthday(LocalDate.of(1991, 2, 2));
        user2.setFriendships(new HashSet<>());

        user3 = new User();
        user3.setId(3);
        user3.setEmail("user3@test.com");
        user3.setLogin("user3");
        user3.setName("User Three");
        user3.setBirthday(LocalDate.of(1992, 3, 3));
        user3.setFriendships(new HashSet<>());

        friendship1 = new Friendship();
        friendship1.setUserId(1);
        friendship1.setFriendId(2);
        friendship1.setStatus(FriendshipStatus.CONFIRMED);

        friendship2 = new Friendship();
        friendship2.setUserId(1);
        friendship2.setFriendId(3);
        friendship2.setStatus(FriendshipStatus.PENDING);
    }

    @Test
    void sendFriendRequest_ShouldAddPendingFriendship() {
        when(userStorage.getById(1)).thenReturn(Optional.of(user1));
        when(userStorage.getById(2)).thenReturn(Optional.of(user2));

        userService.sendFriendRequest(1, 2);

        assertEquals(1, user1.getFriendships().size());
        Friendship added = user1.getFriendships().iterator().next();
        assertEquals(1, added.getUserId());
        assertEquals(2, added.getFriendId());
        assertEquals(FriendshipStatus.PENDING, added.getStatus());
        verify(userStorage, times(2)).getById(anyInt());
    }

    @Test
    void sendFriendRequest_WhenUserNotFound_ShouldThrowException() {
        when(userStorage.getById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.sendFriendRequest(1, 2));
        verify(userStorage, times(1)).getById(1);
        verify(userStorage, never()).getById(2);
    }

    @Test
    void acceptFriendRequest_ShouldConfirmFriendship() {
        Friendship pendingRequest = new Friendship();
        pendingRequest.setUserId(2);
        pendingRequest.setFriendId(1);
        pendingRequest.setStatus(FriendshipStatus.PENDING);
        user1.getFriendships().add(pendingRequest);

        when(userStorage.getById(1)).thenReturn(Optional.of(user1));
        when(userStorage.getById(2)).thenReturn(Optional.of(user2));

        userService.acceptFriendRequest(1, 2);

        assertEquals(FriendshipStatus.CONFIRMED, pendingRequest.getStatus());
        assertEquals(1, user2.getFriendships().size());
        Friendship confirmed = user2.getFriendships().iterator().next();
        assertEquals(1, confirmed.getUserId());
        assertEquals(2, confirmed.getFriendId());
        assertEquals(FriendshipStatus.CONFIRMED, confirmed.getStatus());
    }

    @Test
    void acceptFriendRequest_WhenRequestNotFound_ShouldThrowException() {
        when(userStorage.getById(1)).thenReturn(Optional.of(user1));
        when(userStorage.getById(2)).thenReturn(Optional.of(user2));

        assertThrows(NotFoundException.class, () -> userService.acceptFriendRequest(1, 2));
    }

    @Test
    void removeFriend_ShouldRemoveFriendship() {
        Friendship friendship = new Friendship();
        friendship.setUserId(1);
        friendship.setFriendId(2);
        friendship.setStatus(FriendshipStatus.CONFIRMED);
        user1.getFriendships().add(friendship);
        user2.getFriendships().add(friendship);

        when(userStorage.getById(1)).thenReturn(Optional.of(user1));
        when(userStorage.getById(2)).thenReturn(Optional.of(user2));

        userService.removeFriend(1, 2);

        assertTrue(user1.getFriendships().isEmpty());
        assertTrue(user2.getFriendships().isEmpty());
    }

    @Test
    void getFriends_ShouldReturnOnlyConfirmedFriends() {
        Friendship confirmed1 = new Friendship();
        confirmed1.setUserId(1);
        confirmed1.setFriendId(2);
        confirmed1.setStatus(FriendshipStatus.CONFIRMED);

        Friendship confirmed2 = new Friendship();
        confirmed2.setUserId(2);
        confirmed2.setFriendId(1);
        confirmed2.setStatus(FriendshipStatus.CONFIRMED);

        Friendship pending = new Friendship();
        pending.setUserId(1);
        pending.setFriendId(3);
        pending.setStatus(FriendshipStatus.PENDING);

        user1.getFriendships().add(confirmed1);
        user1.getFriendships().add(pending);
        user2.getFriendships().add(confirmed2);

        when(userStorage.getById(1)).thenReturn(Optional.of(user1));
        when(userStorage.getById(2)).thenReturn(Optional.of(user2));

        List<User> friends = userService.getFriends(1);

        assertEquals(1, friends.size());
        assertEquals(user2, friends.get(0));
    }

    @Test
    void getFriends_WhenNoConfirmedFriends_ShouldReturnEmptyList() {
        Friendship pending = new Friendship();
        pending.setUserId(1);
        pending.setFriendId(2);
        pending.setStatus(FriendshipStatus.PENDING);
        user1.getFriendships().add(pending);

        when(userStorage.getById(1)).thenReturn(Optional.of(user1));

        List<User> friends = userService.getFriends(1);

        assertTrue(friends.isEmpty());
    }

    @Test
    void getPendingRequests_ShouldReturnPendingRequests() {
        Friendship pending1 = new Friendship();
        pending1.setUserId(2);
        pending1.setFriendId(1);
        pending1.setStatus(FriendshipStatus.PENDING);

        Friendship pending2 = new Friendship();
        pending2.setUserId(3);
        pending2.setFriendId(1);
        pending2.setStatus(FriendshipStatus.PENDING);

        user1.getFriendships().add(pending1);
        user1.getFriendships().add(pending2);

        when(userStorage.getById(1)).thenReturn(Optional.of(user1));
        when(userStorage.getById(2)).thenReturn(Optional.of(user2));
        when(userStorage.getById(3)).thenReturn(Optional.of(user3));

        List<User> pendingRequests = userService.getPendingRequests(1);

        assertEquals(2, pendingRequests.size());
        assertTrue(pendingRequests.contains(user2));
        assertTrue(pendingRequests.contains(user3));
    }

    @Test
    void getCommonFriends_ShouldReturnIntersection() {
        Friendship user1Friend2 = new Friendship();
        user1Friend2.setUserId(1);
        user1Friend2.setFriendId(2);
        user1Friend2.setStatus(FriendshipStatus.CONFIRMED);

        Friendship user1Friend3 = new Friendship();
        user1Friend3.setUserId(1);
        user1Friend3.setFriendId(3);
        user1Friend3.setStatus(FriendshipStatus.CONFIRMED);

        Friendship user2Friend1 = new Friendship();
        user2Friend1.setUserId(2);
        user2Friend1.setFriendId(1);
        user2Friend1.setStatus(FriendshipStatus.CONFIRMED);

        Friendship user2Friend3 = new Friendship();
        user2Friend3.setUserId(2);
        user2Friend3.setFriendId(3);
        user2Friend3.setStatus(FriendshipStatus.CONFIRMED);

        user1.getFriendships().add(user1Friend2);
        user1.getFriendships().add(user1Friend3);
        user2.getFriendships().add(user2Friend1);
        user2.getFriendships().add(user2Friend3);

        when(userStorage.getById(1)).thenReturn(Optional.of(user1));
        when(userStorage.getById(2)).thenReturn(Optional.of(user2));
        when(userStorage.getById(3)).thenReturn(Optional.of(user3));

        List<User> commonFriends = userService.getCommonFriends(1, 2);

        assertEquals(1, commonFriends.size());
        assertEquals(user3, commonFriends.get(0));
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        when(userStorage.getAll()).thenReturn(List.of(user1, user2));

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userStorage, times(1)).getAll();
    }

    @Test
    void getUserById_ShouldReturnUser() {
        when(userStorage.getById(1)).thenReturn(Optional.of(user1));

        User result = userService.getUserById(1);

        assertEquals(user1, result);
        verify(userStorage, times(1)).getById(1);
    }

    @Test
    void getUserById_WhenNotFound_ShouldThrowException() {
        when(userStorage.getById(999)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(999));
    }

    @Test
    void createUser_ShouldAddUser() {
        when(userStorage.add(any(User.class))).thenReturn(user1);

        User result = userService.createUser(user1);

        assertEquals(user1, result);
        verify(userStorage, times(1)).add(user1);
    }

    @Test
    void updateUser_ShouldUpdateUser() {
        when(userStorage.update(any(User.class))).thenReturn(user1);

        User result = userService.updateUser(user1);

        assertEquals(user1, result);
        verify(userStorage, times(1)).update(user1);
    }
}