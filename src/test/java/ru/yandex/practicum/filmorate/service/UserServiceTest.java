package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.controller.NotFoundException;
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

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1);
        user1.setEmail("user1@test.com");
        user1.setLogin("user1");
        user1.setName("User One");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        user1.setFriends(new HashSet<>());

        user2 = new User();
        user2.setId(2);
        user2.setEmail("user2@test.com");
        user2.setLogin("user2");
        user2.setName("User Two");
        user2.setBirthday(LocalDate.of(1991, 2, 2));
        user2.setFriends(new HashSet<>());

        user3 = new User();
        user3.setId(3);
        user3.setEmail("user3@test.com");
        user3.setLogin("user3");
        user3.setName("User Three");
        user3.setBirthday(LocalDate.of(1992, 3, 3));
        user3.setFriends(new HashSet<>());
    }

    @Test
    void addFriend_ShouldAddBidirectionalFriendship() {
        when(userStorage.getById(1)).thenReturn(Optional.of(user1));
        when(userStorage.getById(2)).thenReturn(Optional.of(user2));

        userService.addFriend(1, 2);

        assertTrue(user1.getFriends().contains(2));
        assertTrue(user2.getFriends().contains(1));
        verify(userStorage, times(2)).getById(anyInt());
    }

    @Test
    void addFriend_WhenUserNotFound_ShouldThrowException() {
        when(userStorage.getById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.addFriend(1, 2));
        verify(userStorage, times(1)).getById(1);
        verify(userStorage, never()).getById(2);
    }

    @Test
    void addFriend_WhenFriendNotFound_ShouldThrowException() {
        when(userStorage.getById(1)).thenReturn(Optional.of(user1));
        when(userStorage.getById(2)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.addFriend(1, 2));
        verify(userStorage, times(1)).getById(1);
        verify(userStorage, times(1)).getById(2);
    }

    @Test
    void addFriend_WhenAlreadyFriends_ShouldNotDuplicate() {
        user1.getFriends().add(2);
        user2.getFriends().add(1);

        when(userStorage.getById(1)).thenReturn(Optional.of(user1));
        when(userStorage.getById(2)).thenReturn(Optional.of(user2));

        userService.addFriend(1, 2);

        assertEquals(1, user1.getFriends().size());
        assertEquals(1, user2.getFriends().size());
    }

    @Test
    void removeFriend_ShouldRemoveBidirectionalFriendship() {
        user1.getFriends().add(2);
        user2.getFriends().add(1);

        when(userStorage.getById(1)).thenReturn(Optional.of(user1));
        when(userStorage.getById(2)).thenReturn(Optional.of(user2));

        userService.removeFriend(1, 2);

        assertFalse(user1.getFriends().contains(2));
        assertFalse(user2.getFriends().contains(1));
    }

    @Test
    void getFriends_ShouldReturnListOfFriends() {
        user1.getFriends().add(2);
        user1.getFriends().add(3);

        when(userStorage.getById(1)).thenReturn(Optional.of(user1));
        when(userStorage.getById(2)).thenReturn(Optional.of(user2));
        when(userStorage.getById(3)).thenReturn(Optional.of(user3));

        List<User> friends = userService.getFriends(1);

        assertEquals(2, friends.size());
        assertTrue(friends.contains(user2));
        assertTrue(friends.contains(user3));
    }

    @Test
    void getFriends_WhenNoFriends_ShouldReturnEmptyList() {
        when(userStorage.getById(1)).thenReturn(Optional.of(user1));

        List<User> friends = userService.getFriends(1);

        assertTrue(friends.isEmpty());
    }

    @Test
    void getCommonFriends_ShouldReturnIntersection() {
        user1.getFriends().add(2);
        user1.getFriends().add(3);
        user2.getFriends().add(1);
        user2.getFriends().add(3);

        Set<Integer> user2Friends = new HashSet<>();
        user2Friends.add(1);
        user2Friends.add(3);
        user2.setFriends(user2Friends);

        when(userStorage.getById(1)).thenReturn(Optional.of(user1));
        when(userStorage.getById(2)).thenReturn(Optional.of(user2));
        when(userStorage.getById(3)).thenReturn(Optional.of(user3));

        List<User> commonFriends = userService.getCommonFriends(1, 2);

        assertEquals(1, commonFriends.size());
        assertEquals(user3, commonFriends.get(0));
    }

    @Test
    void getCommonFriends_WhenNoCommon_ShouldReturnEmptyList() {
        user1.getFriends().add(2);
        user2.getFriends().add(1);

        when(userStorage.getById(1)).thenReturn(Optional.of(user1));
        when(userStorage.getById(2)).thenReturn(Optional.of(user2));

        List<User> commonFriends = userService.getCommonFriends(1, 2);

        assertTrue(commonFriends.isEmpty());
    }
}
