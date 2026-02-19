package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserStorageTest {

    private InMemoryUserStorage userStorage;
    private User testUser;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
        testUser = new User();
        testUser.setEmail("test@test.com");
        testUser.setLogin("testlogin");
        testUser.setName("Test User");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    void add_ShouldAssignIdAndStoreUser() {
        User added = userStorage.add(testUser);

        assertEquals(1, added.getId());
        assertEquals(testUser.getEmail(), added.getEmail());

        Optional<User> retrieved = userStorage.getById(1);
        assertTrue(retrieved.isPresent());
        assertEquals(testUser.getEmail(), retrieved.get().getEmail());
    }

    @Test
    void add_ShouldIncrementId() {
        User user1 = new User();
        user1.setEmail("user1@test.com");
        user1.setLogin("user1");
        user1.setName("User 1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));

        User user2 = new User();
        user2.setEmail("user2@test.com");
        user2.setLogin("user2");
        user2.setName("User 2");
        user2.setBirthday(LocalDate.of(1991, 1, 1));

        User added1 = userStorage.add(user1);
        User added2 = userStorage.add(user2);

        assertEquals(1, added1.getId());
        assertEquals(2, added2.getId());
    }

    @Test
    void update_ShouldUpdateExistingUser() {
        User added = userStorage.add(testUser);
        added.setName("Updated Name");

        User updated = userStorage.update(added);

        assertEquals("Updated Name", updated.getName());
        Optional<User> retrieved = userStorage.getById(added.getId());
        assertTrue(retrieved.isPresent());
        assertEquals("Updated Name", retrieved.get().getName());
    }

    @Test
    void update_WhenUserNotFound_ShouldThrowException() {
        testUser.setId(999);

        assertThrows(NotFoundException.class, () -> userStorage.update(testUser));
    }

    @Test
    void delete_ShouldRemoveUser() {
        User added = userStorage.add(testUser);

        boolean result = userStorage.delete(added.getId());

        assertTrue(result);
        assertTrue(userStorage.getById(added.getId()).isEmpty());
    }

    @Test
    void delete_WhenUserNotFound_ShouldReturnFalse() {
        boolean result = userStorage.delete(999);

        assertFalse(result);
    }

    @Test
    void getAll_ShouldReturnAllUsers() {
        userStorage.add(testUser);
        userStorage.add(testUser);

        List<User> all = userStorage.getAll();

        assertEquals(2, all.size());
    }

    @Test
    void getById_ShouldReturnEmptyWhenNotFound() {
        Optional<User> retrieved = userStorage.getById(999);

        assertTrue(retrieved.isEmpty());
    }
}
