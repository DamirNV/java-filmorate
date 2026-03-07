package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.dto.user.CreateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserResponse;
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

    private CreateUserRequest createRequest;
    private UserResponse userResponse;
    private UpdateUserRequest updateRequest;

    @BeforeEach
    void setUp() {
        createRequest = new CreateUserRequest();
        createRequest.setEmail("test@example.com");
        createRequest.setLogin("testlogin");
        createRequest.setName("Test User");
        createRequest.setBirthday(LocalDate.of(2000, 1, 1));

        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setEmail("test@example.com");
        userResponse.setLogin("testlogin");
        userResponse.setName("Test User");
        userResponse.setBirthday(LocalDate.of(2000, 1, 1));

        updateRequest = new UpdateUserRequest();
        updateRequest.setId(1L);
        updateRequest.setEmail("updated@example.com");
        updateRequest.setLogin("updatedlogin");
        updateRequest.setName("Updated User");
        updateRequest.setBirthday(LocalDate.of(2000, 1, 1));
    }

    @Test
    void createUser_ShouldReturnCreatedUser() throws Exception {
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.login").value("testlogin"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void getAllUsers_ShouldReturnList() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(userResponse));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("test@example.com"));
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        when(userService.getUserById(1)).thenReturn(userResponse);

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
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        UserResponse updatedResponse = new UserResponse();
        updatedResponse.setId(1L);
        updatedResponse.setEmail("updated@example.com");
        updatedResponse.setLogin("updatedlogin");
        updatedResponse.setName("Updated User");
        updatedResponse.setBirthday(LocalDate.of(2000, 1, 1));

        when(userService.updateUser(any(UpdateUserRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated User"));
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
        when(userService.getFriends(1)).thenReturn(List.of(userResponse));

        mockMvc.perform(get("/users/{id}/friends", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("test@example.com"));
    }

    @Test
    void getCommonFriends_ShouldReturnList() throws Exception {
        when(userService.getCommonFriends(1, 2)).thenReturn(List.of(userResponse));

        mockMvc.perform(get("/users/{id}/friends/common/{otherId}", 1, 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("test@example.com"));
    }
}