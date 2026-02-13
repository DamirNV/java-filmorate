package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = new User();
        validUser.setEmail("test@example.com");
        validUser.setLogin("testlogin");
        validUser.setName("Test User");
        validUser.setBirthday(LocalDate.of(2000, 1, 1));
    }

    @Test
    void createValidUserShouldReturnOk() throws Exception {
        User createdUser = new User();
        createdUser.setId(1);
        createdUser.setEmail("test@example.com");
        createdUser.setLogin("testlogin");
        createdUser.setName("Test User");
        createdUser.setBirthday(LocalDate.of(2000, 1, 1));

        when(userService.createUser(any(User.class))).thenReturn(createdUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void getAllUsersShouldReturnOk() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(validUser));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    void createUserWithEmptyEmailShouldReturnBadRequest() throws Exception {
        validUser.setEmail("");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void createUserWithNullEmailShouldReturnBadRequest() throws Exception {
        validUser.setEmail(null);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void createUserWithInvalidEmailShouldReturnBadRequest() throws Exception {
        validUser.setEmail("invalid-email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void createUserWithEmptyLoginShouldReturnBadRequest() throws Exception {
        validUser.setLogin("");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void createUserWithNullLoginShouldReturnBadRequest() throws Exception {
        validUser.setLogin(null);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void createUserWithLoginContainingSpacesShouldReturnBadRequest() throws Exception {
        validUser.setLogin("login with spaces");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void createUserWithEmptyNameShouldUseLogin() throws Exception {
        validUser.setName("");
        User createdUser = new User();
        createdUser.setId(1);
        createdUser.setEmail("test@example.com");
        createdUser.setLogin("testlogin");
        createdUser.setName("testlogin");
        createdUser.setBirthday(LocalDate.of(2000, 1, 1));

        when(userService.createUser(any(User.class))).thenReturn(createdUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("testlogin"));
    }

    @Test
    void createUserWithNullNameShouldUseLogin() throws Exception {
        validUser.setName(null);
        User createdUser = new User();
        createdUser.setId(1);
        createdUser.setEmail("test@example.com");
        createdUser.setLogin("testlogin");
        createdUser.setName("testlogin");
        createdUser.setBirthday(LocalDate.of(2000, 1, 1));

        when(userService.createUser(any(User.class))).thenReturn(createdUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("testlogin"));
    }

    @Test
    void createUserWithBlankNameShouldUseLogin() throws Exception {
        validUser.setName("   ");
        User createdUser = new User();
        createdUser.setId(1);
        createdUser.setEmail("test@example.com");
        createdUser.setLogin("testlogin");
        createdUser.setName("testlogin");
        createdUser.setBirthday(LocalDate.of(2000, 1, 1));

        when(userService.createUser(any(User.class))).thenReturn(createdUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("testlogin"));
    }

    @Test
    void createUserWithFutureBirthdayShouldReturnBadRequest() throws Exception {
        validUser.setBirthday(LocalDate.now().plusDays(1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void createUserWithTodayBirthdayShouldReturnOk() throws Exception {
        validUser.setBirthday(LocalDate.now());
        User createdUser = new User();
        createdUser.setId(1);
        createdUser.setEmail("test@example.com");
        createdUser.setLogin("testlogin");
        createdUser.setName("Test User");
        createdUser.setBirthday(LocalDate.now());

        when(userService.createUser(any(User.class))).thenReturn(createdUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isOk());
    }

    @Test
    void updateUserShouldReturnOk() throws Exception {
        User updatedUser = new User();
        updatedUser.setId(1);
        updatedUser.setEmail("test@example.com");
        updatedUser.setLogin("testlogin");
        updatedUser.setName("Updated Name");
        updatedUser.setBirthday(LocalDate.of(2000, 1, 1));

        when(userService.updateUser(any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void updateUserWithEmptyNameShouldUseLogin() throws Exception {
        User updatedUser = new User();
        updatedUser.setId(1);
        updatedUser.setEmail("test@example.com");
        updatedUser.setLogin("testlogin");
        updatedUser.setName("");
        updatedUser.setBirthday(LocalDate.of(2000, 1, 1));

        User resultUser = new User();
        resultUser.setId(1);
        resultUser.setEmail("test@example.com");
        resultUser.setLogin("testlogin");
        resultUser.setName("testlogin");
        resultUser.setBirthday(LocalDate.of(2000, 1, 1));

        when(userService.updateUser(any(User.class))).thenReturn(resultUser);

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("testlogin"));
    }

    @Test
    void updateUserWithNullNameShouldUseLogin() throws Exception {
        User updatedUser = new User();
        updatedUser.setId(1);
        updatedUser.setEmail("test@example.com");
        updatedUser.setLogin("testlogin");
        updatedUser.setName(null);
        updatedUser.setBirthday(LocalDate.of(2000, 1, 1));

        User resultUser = new User();
        resultUser.setId(1);
        resultUser.setEmail("test@example.com");
        resultUser.setLogin("testlogin");
        resultUser.setName("testlogin");
        resultUser.setBirthday(LocalDate.of(2000, 1, 1));

        when(userService.updateUser(any(User.class))).thenReturn(resultUser);

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("testlogin"));
    }

    @Test
    void createUserWithEmptyBodyShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void updateNonExistentUserShouldThrowException() throws Exception {
        validUser.setId(999);
        when(userService.updateUser(any(User.class)))
                .thenThrow(new NotFoundException("Пользователь с id=999 не найден"));

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        User createdUser = new User();
        createdUser.setId(1);
        createdUser.setEmail("test@example.com");
        createdUser.setLogin("testlogin");
        createdUser.setName("Test User");
        createdUser.setBirthday(LocalDate.of(2000, 1, 1));

        when(userService.getUserById(1)).thenReturn(createdUser);

        mockMvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getUserById_WhenNotFound_ShouldReturn404() throws Exception {
        when(userService.getUserById(999))
                .thenThrow(new NotFoundException("Пользователь с id=999 не найден"));

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void addFriend_ShouldReturnOk() throws Exception {
        doNothing().when(userService).addFriend(1, 2);

        mockMvc.perform(put("/users/{id}/friends/{friendId}", 1, 2))
                .andExpect(status().isOk());
    }

    @Test
    void removeFriend_ShouldReturnOk() throws Exception {
        doNothing().when(userService).removeFriend(1, 2);

        mockMvc.perform(delete("/users/{id}/friends/{friendId}", 1, 2))
                .andExpect(status().isOk());
    }

    @Test
    void getFriends_ShouldReturnList() throws Exception {
        when(userService.getFriends(1)).thenReturn(List.of(validUser));

        mockMvc.perform(get("/users/{id}/friends", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getCommonFriends_ShouldReturnList() throws Exception {
        when(userService.getCommonFriends(1, 2)).thenReturn(List.of(validUser));

        mockMvc.perform(get("/users/{id}/friends/common/{otherId}", 1, 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}