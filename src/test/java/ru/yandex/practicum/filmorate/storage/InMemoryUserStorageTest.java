package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserStorageTest {

    private InMemoryUserStorage userStorage;
    private User testUser;
    private User testUser2;
    private Friendship testFriendship;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();

        testUser = new User();
        testUser.setEmail("test@test.com");
        testUser.setLogin("testlogin");
        testUser.setName("Test User");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
        testUser.setFriendships(new HashSet<>());

        testUser2 = new User();
        testUser2.setEmail("test2@test.com");
        testUser2.setLogin("testlogin2");
        testUser2.setName("Test User 2");
        testUser2.setBirthday(LocalDate.of(1991, 2, 2));
        testUser2.setFriendships(new HashSet<>());

        testFriendship = new Friendship();
        testFriendship.setUserId(1);
        testFriendship.setFriendId(2);
        testFriendship.setStatus(FriendshipStatus.PENDING);
    }

    @Test
    void add_ShouldAssignIdAndStoreUser() {
        User added = userStorage.add(testUser);

        assertEquals(1, added.getId());
        assertEquals(testUser.getEmail(), added.getEmail());
        assertNotNull(added.getFriendships());
        assertTrue(added.getFriendships().isEmpty());

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
        user1.setFriendships(new HashSet<>());

        User user2 = new User();
        user2.setEmail("user2@test.com");
        user2.setLogin("user2");
        user2.setName("User 2");
        user2.setBirthday(LocalDate.of(1991, 1, 1));
        user2.setFriendships(new HashSet<>());

        User added1 = userStorage.add(user1);
        User added2 = userStorage.add(user2);

        assertEquals(1, added1.getId());
        assertEquals(2, added2.getId());
    }

    @Test
    void update_ShouldUpdateExistingUser() {
        User added = userStorage.add(testUser);
        added.setName("Updated Name");

        Friendship friendship = new Friendship();
        friendship.setUserId(added.getId());
        friendship.setFriendId(2);
        friendship.setStatus(FriendshipStatus.CONFIRMED);
        added.getFriendships().add(friendship);

        User updated = userStorage.update(added);

        assertEquals("Updated Name", updated.getName());
        assertEquals(1, updated.getFriendships().size());

        Optional<User> retrieved = userStorage.getById(added.getId());
        assertTrue(retrieved.isPresent());
        assertEquals("Updated Name", retrieved.get().getName());
        assertEquals(1, retrieved.get().getFriendships().size());
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
        userStorage.add(testUser2);

        List<User> all = userStorage.getAll();

        assertEquals(2, all.size());
        assertTrue(all.stream().allMatch(u -> u.getFriendships() != null));
    }

    @Test
    void getById_ShouldReturnUserWithFriendships() {
        User added = userStorage.add(testUser);

        Friendship friendship = new Friendship();
        friendship.setUserId(added.getId());
        friendship.setFriendId(2);
        friendship.setStatus(FriendshipStatus.PENDING);
        added.getFriendships().add(friendship);
        userStorage.update(added);

        Optional<User> retrieved = userStorage.getById(added.getId());

        assertTrue(retrieved.isPresent());
        User user = retrieved.get();
        assertEquals(1, user.getFriendships().size());
        Friendship retrievedFriendship = user.getFriendships().iterator().next();
        assertEquals(added.getId(), retrievedFriendship.getUserId());
        assertEquals(2, retrievedFriendship.getFriendId());
        assertEquals(FriendshipStatus.PENDING, retrievedFriendship.getStatus());
    }

    @Test
    void getById_ShouldReturnEmptyWhenNotFound() {
        Optional<User> retrieved = userStorage.getById(999);

        assertTrue(retrieved.isEmpty());
    }

    @Test
    void add_ShouldHandleUserWithExistingFriendships() {
        Set<Friendship> friendships = new HashSet<>();
        friendships.add(testFriendship);
        testUser.setFriendships(friendships);

        User added = userStorage.add(testUser);

        assertEquals(1, added.getId());
        assertEquals(1, added.getFriendships().size());
        Friendship addedFriendship = added.getFriendships().iterator().next();
        assertEquals(1, addedFriendship.getUserId());
        assertEquals(2, addedFriendship.getFriendId());
    }

    @Test
    void update_ShouldPreserveExistingFriendships() {
        User added = userStorage.add(testUser);

        Friendship friendship1 = new Friendship();
        friendship1.setUserId(added.getId());
        friendship1.setFriendId(2);
        friendship1.setStatus(FriendshipStatus.PENDING);
        added.getFriendships().add(friendship1);
        userStorage.update(added);

        Friendship friendship2 = new Friendship();
        friendship2.setUserId(3);
        friendship2.setFriendId(added.getId());
        friendship2.setStatus(FriendshipStatus.PENDING);
        added.getFriendships().add(friendship2);

        User updated = userStorage.update(added);

        assertEquals(2, updated.getFriendships().size());
    }

    @Test
    void add_MultipleUsersWithFriendships_ShouldMaintainIndependence() {
        User user1 = userStorage.add(testUser);
        User user2 = userStorage.add(testUser2);

        Friendship friendship = new Friendship();
        friendship.setUserId(user1.getId());
        friendship.setFriendId(user2.getId());
        friendship.setStatus(FriendshipStatus.PENDING);

        user1.getFriendships().add(friendship);
        userStorage.update(user1);

        Optional<User> retrieved1 = userStorage.getById(user1.getId());
        Optional<User> retrieved2 = userStorage.getById(user2.getId());

        assertTrue(retrieved1.isPresent());
        assertTrue(retrieved2.isPresent());
        assertEquals(1, retrieved1.get().getFriendships().size());
        assertEquals(0, retrieved2.get().getFriendships().size());
    }
}