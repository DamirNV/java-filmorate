package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;
    private Friendship friendship;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setEmail("test@test.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        user.setFriendships(new HashSet<>());

        friendship = new Friendship();
        friendship.setUserId(1);
        friendship.setFriendId(2);
        friendship.setStatus(FriendshipStatus.CONFIRMED);
    }

    @Test
    void constructorAndGetters_ShouldWork() {
        assertEquals(1, user.getId());
        assertEquals("test@test.com", user.getEmail());
        assertEquals("testlogin", user.getLogin());
        assertEquals("Test User", user.getName());
        assertEquals(LocalDate.of(1990, 1, 1), user.getBirthday());
        assertNotNull(user.getFriendships());
        assertTrue(user.getFriendships().isEmpty());
    }

    @Test
    void setters_ShouldWork() {
        user.setId(2);
        user.setEmail("new@test.com");
        user.setLogin("newlogin");
        user.setName("New User");
        user.setBirthday(LocalDate.of(1991, 1, 1));

        assertEquals(2, user.getId());
        assertEquals("new@test.com", user.getEmail());
        assertEquals("newlogin", user.getLogin());
        assertEquals("New User", user.getName());
        assertEquals(LocalDate.of(1991, 1, 1), user.getBirthday());
    }

    @Test
    void friendships_ShouldBeMutable() {
        user.getFriendships().add(friendship);
        assertEquals(1, user.getFriendships().size());
        assertTrue(user.getFriendships().contains(friendship));

        user.getFriendships().clear();
        assertTrue(user.getFriendships().isEmpty());
    }

    @Test
    void equals_SameId_ShouldReturnTrue() {
        User user2 = new User();
        user2.setId(1);
        user2.setEmail("different@test.com");
        user2.setLogin("different");
        user2.setName("Different");

        assertTrue(user.equals(user2));
    }

    @Test
    void equals_DifferentId_ShouldReturnFalse() {
        User user2 = new User();
        user2.setId(2);
        user2.setEmail("test@test.com");
        user2.setLogin("testlogin");
        user2.setName("Test User");

        assertFalse(user.equals(user2));
    }

    @Test
    void equals_SameObject_ShouldReturnTrue() {
        assertTrue(user.equals(user));
    }

    @Test
    void equals_Null_ShouldReturnFalse() {
        assertFalse(user.equals(null));
    }

    @Test
    void hashCode_SameId_ShouldBeEqual() {
        User user2 = new User();
        user2.setId(1);

        assertEquals(user.hashCode(), user2.hashCode());
    }

    @Test
    void hashCode_DifferentId_ShouldBeDifferent() {
        User user2 = new User();
        user2.setId(2);

        assertNotEquals(user.hashCode(), user2.hashCode());
    }

    @Test
    void toString_ShouldNotBeEmpty() {
        assertNotNull(user.toString());
        assertFalse(user.toString().isEmpty());
    }

    @Test
    void user_WithNullName_ShouldBeValid() {
        user.setName(null);
        assertNull(user.getName());
    }

    @Test
    void user_WithEmptyName_ShouldBeValid() {
        user.setName("");
        assertEquals("", user.getName());
    }

    @Test
    void user_WithNullFriendships_ShouldBeValid() {
        user.setFriendships(null);
        assertNull(user.getFriendships());
    }

    @Test
    void friendship_ShouldWork() {
        assertEquals(1, friendship.getUserId());
        assertEquals(2, friendship.getFriendId());
        assertEquals(FriendshipStatus.CONFIRMED, friendship.getStatus());
    }

    @Test
    void friendship_Setters_ShouldWork() {
        friendship.setUserId(3);
        friendship.setFriendId(4);
        friendship.setStatus(FriendshipStatus.CONFIRMED);

        assertEquals(3, friendship.getUserId());
        assertEquals(4, friendship.getFriendId());
        assertEquals(FriendshipStatus.CONFIRMED, friendship.getStatus());
    }
}