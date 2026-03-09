package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.controller.NotFoundException;
import ru.yandex.practicum.filmorate.dal.repositories.UserRepository;
import ru.yandex.practicum.filmorate.dto.user.CreateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserResponse;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user1;
    private User user2;
    private User user3;
    private CreateUserRequest createRequest;
    private UpdateUserRequest updateRequest;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1);
        user1.setEmail("user1@test.com");
        user1.setLogin("user1");
        user1.setName("User One");
        user1.setBirthday(LocalDate.of(1990, 1, 1));

        user2 = new User();
        user2.setId(2);
        user2.setEmail("user2@test.com");
        user2.setLogin("user2");
        user2.setName("User Two");
        user2.setBirthday(LocalDate.of(1991, 2, 2));

        user3 = new User();
        user3.setId(3);
        user3.setEmail("user3@test.com");
        user3.setLogin("user3");
        user3.setName("User Three");
        user3.setBirthday(LocalDate.of(1992, 3, 3));

        createRequest = new CreateUserRequest();
        createRequest.setEmail("new@test.com");
        createRequest.setLogin("newlogin");
        createRequest.setName("New User");
        createRequest.setBirthday(LocalDate.of(1995, 1, 1));

        updateRequest = new UpdateUserRequest();
        updateRequest.setId(1L);
        updateRequest.setEmail("updated@test.com");
        updateRequest.setLogin("updatedlogin");
        updateRequest.setName("Updated User");
        updateRequest.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserResponse> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("user1@test.com", result.get(0).getEmail());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_ShouldReturnUser() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user1));

        UserResponse result = userService.getUserById(1);

        assertEquals("user1@test.com", result.getEmail());
        assertEquals("User One", result.getName());
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void getUserById_WhenNotFound_ShouldThrowException() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(999));
    }

    @Test
    void createUser_ShouldCreateAndReturnUser() {
        when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user1);

        UserResponse result = userService.createUser(createRequest);

        assertNotNull(result);
        assertEquals("user1@test.com", result.getEmail());
        verify(userRepository, times(1)).findByEmail("new@test.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_WithDuplicateEmail_ShouldThrowException() {
        when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.of(user1));

        assertThrows(DuplicatedDataException.class, () -> userService.createUser(createRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_WithEmptyName_ShouldUseLogin() {
        createRequest.setName("");

        User userWithLoginAsName = new User();
        userWithLoginAsName.setId(1);
        userWithLoginAsName.setEmail("new@test.com");
        userWithLoginAsName.setLogin("newlogin");
        userWithLoginAsName.setName("newlogin");
        userWithLoginAsName.setBirthday(LocalDate.of(1995, 1, 1));

        when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(userWithLoginAsName);

        UserResponse result = userService.createUser(createRequest);

        assertEquals("newlogin", result.getName());
    }

    @Test
    void createUser_WithNullName_ShouldUseLogin() {
        createRequest.setName(null);

        User userWithLoginAsName = new User();
        userWithLoginAsName.setId(1);
        userWithLoginAsName.setEmail("new@test.com");
        userWithLoginAsName.setLogin("newlogin");
        userWithLoginAsName.setName("newlogin");
        userWithLoginAsName.setBirthday(LocalDate.of(1995, 1, 1));

        when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(userWithLoginAsName);

        UserResponse result = userService.createUser(createRequest);

        assertEquals("newlogin", result.getName());
    }

    @Test
    void updateUser_ShouldUpdateAndReturnUser() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(userRepository.findByEmail("updated@test.com")).thenReturn(Optional.empty());
        when(userRepository.update(any(User.class))).thenReturn(user1);

        UserResponse result = userService.updateUser(updateRequest);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(1);
        verify(userRepository, times(1)).update(any(User.class));
    }

    @Test
    void updateUser_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(updateRequest));
        verify(userRepository, never()).update(any(User.class));
    }

    @Test
    void updateUser_WithoutId_ShouldThrowException() {
        updateRequest.setId(null);

        assertThrows(NotFoundException.class, () -> userService.updateUser(updateRequest));
    }

    @Test
    void updateUser_WithDuplicateEmail_ShouldThrowException() {
        User existingUser = new User();
        existingUser.setId(2);
        existingUser.setEmail("updated@test.com");

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(userRepository.findByEmail("updated@test.com")).thenReturn(Optional.of(existingUser));

        assertThrows(DuplicatedDataException.class, () -> userService.updateUser(updateRequest));
        verify(userRepository, never()).update(any(User.class));
    }

    @Test
    void updateUser_WithSameEmail_ShouldNotCheckDuplicate() {
        user1.setEmail("updated@test.com");

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(userRepository.update(any(User.class))).thenReturn(user1);

        UserResponse result = userService.updateUser(updateRequest);

        assertNotNull(result);
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void updateUser_WithEmptyName_ShouldUseLogin() {
        updateRequest.setName("");

        User updatedUser = new User();
        updatedUser.setId(1);
        updatedUser.setEmail("updated@test.com");
        updatedUser.setLogin("updatedlogin");
        updatedUser.setName("updatedlogin");
        updatedUser.setBirthday(LocalDate.of(1990, 1, 1));

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(userRepository.findByEmail("updated@test.com")).thenReturn(Optional.empty());
        when(userRepository.update(any(User.class))).thenReturn(updatedUser);

        UserResponse result = userService.updateUser(updateRequest);

        assertEquals("updatedlogin", result.getName());
    }

    @Test
    void addFriend_ShouldAddFriend() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2)).thenReturn(Optional.of(user2));
        doNothing().when(userRepository).addFriend(1, 2);

        userService.addFriend(1, 2);

        verify(userRepository, times(1)).addFriend(1, 2);
    }

    @Test
    void addFriend_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.addFriend(1, 2));
        verify(userRepository, never()).addFriend(anyInt(), anyInt());
    }

    @Test
    void addFriend_WhenFriendNotFound_ShouldThrowException() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.addFriend(1, 2));
        verify(userRepository, never()).addFriend(anyInt(), anyInt());
    }

    @Test
    void removeFriend_ShouldRemoveFriend() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2)).thenReturn(Optional.of(user2));
        doNothing().when(userRepository).removeFriend(1, 2);

        userService.removeFriend(1, 2);

        verify(userRepository, times(1)).removeFriend(1, 2);
    }

    @Test
    void removeFriend_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.removeFriend(1, 2));
        verify(userRepository, never()).removeFriend(anyInt(), anyInt());
    }

    @Test
    void removeFriend_WhenFriendNotFound_ShouldThrowException() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.removeFriend(1, 2));
        verify(userRepository, never()).removeFriend(anyInt(), anyInt());
    }

    @Test
    void getFriends_ShouldReturnFriendsList() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(userRepository.getFriends(1)).thenReturn(List.of(user2, user3));

        List<UserResponse> friends = userService.getFriends(1);

        assertEquals(2, friends.size());
        assertEquals("user2@test.com", friends.get(0).getEmail());
        verify(userRepository, times(1)).getFriends(1);
    }

    @Test
    void getFriends_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getFriends(1));
        verify(userRepository, never()).getFriends(anyInt());
    }

    @Test
    void getCommonFriends_ShouldReturnCommonFriends() {
        when(userRepository.getCommonFriends(1, 2)).thenReturn(List.of(user3));

        List<UserResponse> common = userService.getCommonFriends(1, 2);

        assertEquals(1, common.size());
        assertEquals("user3@test.com", common.get(0).getEmail());
        verify(userRepository, times(1)).getCommonFriends(1, 2);
    }
}