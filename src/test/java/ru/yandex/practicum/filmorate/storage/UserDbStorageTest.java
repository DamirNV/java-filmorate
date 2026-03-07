package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@ContextConfiguration(classes = {UserDbStorage.class})
class UserDbStorageTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private UserDbStorage userStorage;

    @BeforeEach
    void setUp() {
        userStorage = new UserDbStorage(jdbcTemplate);
    }

    @Test
    void testAddUser() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User created = userStorage.add(user);

        assertThat(created.getId()).isPositive();
        assertThat(created.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    void testGetUserById() {
        User user = new User();
        user.setEmail("get@test.com");
        user.setLogin("getlogin");
        user.setName("Get User");
        user.setBirthday(LocalDate.of(1991, 2, 2));

        User created = userStorage.add(user);
        Optional<User> found = userStorage.getById(created.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("get@test.com");
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setEmail("update@test.com");
        user.setLogin("updatelogin");
        user.setName("Update User");
        user.setBirthday(LocalDate.of(1992, 3, 3));

        User created = userStorage.add(user);
        created.setName("Updated Name");

        User updated = userStorage.update(created);

        assertThat(updated.getName()).isEqualTo("Updated Name");
        Optional<User> found = userStorage.getById(created.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Updated Name");
    }

    @Test
    void testDeleteUser() {
        User user = new User();
        user.setEmail("delete@test.com");
        user.setLogin("deletelogin");
        user.setName("Delete User");
        user.setBirthday(LocalDate.of(1993, 4, 4));

        User created = userStorage.add(user);
        boolean deleted = userStorage.delete(created.getId());

        assertTrue(deleted);
        Optional<User> found = userStorage.getById(created.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void testGetAllUsers() {
        User user1 = new User();
        user1.setEmail("all1@test.com");
        user1.setLogin("all1");
        user1.setName("All User 1");
        user1.setBirthday(LocalDate.of(1994, 5, 5));

        User user2 = new User();
        user2.setEmail("all2@test.com");
        user2.setLogin("all2");
        user2.setName("All User 2");
        user2.setBirthday(LocalDate.of(1995, 6, 6));

        userStorage.add(user1);
        userStorage.add(user2);

        List<User> users = userStorage.getAll();

        assertThat(users).hasSize(2);
    }
}