package ru.yandex.practicum.filmorate.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.user.CreateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserResponse;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private User user;
    private CreateUserRequest createRequest;
    private UpdateUserRequest updateRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setEmail("user@test.com");
        user.setLogin("userlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

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
        updateRequest.setBirthday(LocalDate.of(1991, 1, 1));
    }

    @Test
    void mapToUser_FromCreateRequest_ShouldMapAllFields() {
        User result = UserMapper.mapToUser(createRequest);

        assertEquals("new@test.com", result.getEmail());
        assertEquals("newlogin", result.getLogin());
        assertEquals("New User", result.getName());
        assertEquals(LocalDate.of(1995, 1, 1), result.getBirthday());
    }

    @Test
    void mapToUser_FromCreateRequest_WithNullName_ShouldMapNull() {
        createRequest.setName(null);

        User result = UserMapper.mapToUser(createRequest);

        assertNull(result.getName());
    }

    @Test
    void mapToUser_FromCreateRequest_WithEmptyName_ShouldMapEmpty() {
        createRequest.setName("");

        User result = UserMapper.mapToUser(createRequest);

        assertEquals("", result.getName());
    }

    @Test
    void updateUserFields_ShouldUpdateOnlyProvidedFields() {
        User result = UserMapper.updateUserFields(user, updateRequest);

        assertEquals("updated@test.com", result.getEmail());
        assertEquals("updatedlogin", result.getLogin());
        assertEquals("Updated User", result.getName());
        assertEquals(LocalDate.of(1991, 1, 1), result.getBirthday());
    }

    @Test
    void updateUserFields_WithNullFields_ShouldKeepOriginalValues() {
        updateRequest.setEmail(null);
        updateRequest.setLogin(null);
        updateRequest.setName(null);
        updateRequest.setBirthday(null);

        User result = UserMapper.updateUserFields(user, updateRequest);

        assertEquals("user@test.com", result.getEmail());
        assertEquals("userlogin", result.getLogin());
        assertEquals("Test User", result.getName());
        assertEquals(LocalDate.of(1990, 1, 1), result.getBirthday());
    }

    @Test
    void updateUserFields_WithEmptyEmail_ShouldNotUpdate() {
        updateRequest.setEmail("");

        User result = UserMapper.updateUserFields(user, updateRequest);

        assertEquals("user@test.com", result.getEmail());
    }

    @Test
    void updateUserFields_WithBlankEmail_ShouldNotUpdate() {
        updateRequest.setEmail("   ");

        User result = UserMapper.updateUserFields(user, updateRequest);

        assertEquals("user@test.com", result.getEmail());
    }

    @Test
    void updateUserFields_WithEmptyLogin_ShouldNotUpdate() {
        updateRequest.setLogin("");

        User result = UserMapper.updateUserFields(user, updateRequest);

        assertEquals("userlogin", result.getLogin());
    }

    @Test
    void updateUserFields_WithBlankLogin_ShouldNotUpdate() {
        updateRequest.setLogin("   ");

        User result = UserMapper.updateUserFields(user, updateRequest);

        assertEquals("userlogin", result.getLogin());
    }

    @Test
    void updateUserFields_WithEmptyName_ShouldNotUpdate() {
        updateRequest.setName("");

        User result = UserMapper.updateUserFields(user, updateRequest);

        assertEquals("Test User", result.getName());
    }

    @Test
    void updateUserFields_WithBlankName_ShouldNotUpdate() {
        updateRequest.setName("   ");

        User result = UserMapper.updateUserFields(user, updateRequest);

        assertEquals("Test User", result.getName());
    }

    @Test
    void mapToUserResponse_ShouldMapAllFields() {
        UserResponse result = UserMapper.mapToUserResponse(user);

        assertEquals(1L, result.getId());
        assertEquals("user@test.com", result.getEmail());
        assertEquals("userlogin", result.getLogin());
        assertEquals("Test User", result.getName());
        assertEquals(LocalDate.of(1990, 1, 1), result.getBirthday());
    }

    @Test
    void mapToUserResponse_WithNullName_ShouldMapNull() {
        user.setName(null);

        UserResponse result = UserMapper.mapToUserResponse(user);

        assertNull(result.getName());
    }

    @Test
    void mapToUserResponse_WithEmptyName_ShouldMapEmpty() {
        user.setName("");

        UserResponse result = UserMapper.mapToUserResponse(user);

        assertEquals("", result.getName());
    }

    @Test
    void mapToUser_AndMapToUserResponse_ShouldBeConsistent() {
        User mappedUser = UserMapper.mapToUser(createRequest);
        mappedUser.setId(1);

        UserResponse response = UserMapper.mapToUserResponse(mappedUser);

        assertEquals(createRequest.getEmail(), response.getEmail());
        assertEquals(createRequest.getLogin(), response.getLogin());
        assertEquals(createRequest.getName(), response.getName());
        assertEquals(createRequest.getBirthday(), response.getBirthday());
    }

    @Test
    void updateUserFields_ShouldPreserveId() {
        User result = UserMapper.updateUserFields(user, updateRequest);

        assertEquals(1, result.getId());
    }

    @Test
    void mapToUserResponse_ShouldPreserveId() {
        UserResponse result = UserMapper.mapToUserResponse(user);

        assertEquals(1L, result.getId());
    }
}