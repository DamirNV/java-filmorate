package ru.yandex.practicum.filmorate.dal.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import({UserRepository.class, UserRowMapper.class})
@Sql(scripts = {"/schema.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private User testUser2;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("user1@test.com");
        testUser.setLogin("user1");
        testUser.setName("User One");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));

        testUser2 = new User();
        testUser2.setEmail("user2@test.com");
        testUser2.setLogin("user2");
        testUser2.setName("User Two");
        testUser2.setBirthday(LocalDate.of(1991, 2, 2));
    }

    @Test
    void save_ShouldCreateUserAndReturnWithId() {
        User savedUser = userRepository.save(testUser);

        assertThat(savedUser.getId()).isPositive();
        assertThat(savedUser.getEmail()).isEqualTo("user1@test.com");
        assertThat(savedUser.getLogin()).isEqualTo("user1");
        assertThat(savedUser.getName()).isEqualTo("User One");

        Optional<User> retrieved = userRepository.findById(savedUser.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getEmail()).isEqualTo("user1@test.com");
    }

    @Test
    void findById_ShouldReturnUser() {
        User savedUser = userRepository.save(testUser);

        Optional<User> found = userRepository.findById(savedUser.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("user1@test.com");
        assertThat(found.get().getLogin()).isEqualTo("user1");
    }

    @Test
    void findById_WhenNotFound_ShouldReturnEmpty() {
        Optional<User> found = userRepository.findById(999);

        assertThat(found).isEmpty();
    }

    @Test
    void findByEmail_ShouldReturnUser() {
        userRepository.save(testUser);

        Optional<User> found = userRepository.findByEmail("user1@test.com");

        assertThat(found).isPresent();
        assertThat(found.get().getLogin()).isEqualTo("user1");
    }

    @Test
    void findByEmail_WhenNotFound_ShouldReturnEmpty() {
        Optional<User> found = userRepository.findByEmail("nonexistent@test.com");

        assertThat(found).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllUsers() {
        userRepository.save(testUser);
        userRepository.save(testUser2);

        List<User> users = userRepository.findAll();

        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getEmail)
                .containsExactlyInAnyOrder("user1@test.com", "user2@test.com");
    }

    @Test
    void update_ShouldUpdateUser() {
        User savedUser = userRepository.save(testUser);
        savedUser.setName("Updated Name");
        savedUser.setLogin("updatedlogin");

        User updatedUser = userRepository.update(savedUser);

        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getLogin()).isEqualTo("updatedlogin");

        Optional<User> retrieved = userRepository.findById(savedUser.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getName()).isEqualTo("Updated Name");
        assertThat(retrieved.get().getLogin()).isEqualTo("updatedlogin");
    }

    @Test
    void delete_ShouldRemoveUser() {
        User savedUser = userRepository.save(testUser);

        boolean deleted = userRepository.delete(savedUser.getId());

        assertTrue(deleted);
        Optional<User> retrieved = userRepository.findById(savedUser.getId());
        assertThat(retrieved).isEmpty();
    }

    @Test
    void delete_WhenNotFound_ShouldReturnFalse() {
        boolean deleted = userRepository.delete(999);

        assertFalse(deleted);
    }

    @Test
    void addFriend_ShouldAddFriendship() {
        User savedUser1 = userRepository.save(testUser);
        User savedUser2 = userRepository.save(testUser2);

        userRepository.addFriend(savedUser1.getId(), savedUser2.getId());

        List<User> friends = userRepository.getFriends(savedUser1.getId());
        assertThat(friends).hasSize(1);
        assertThat(friends.get(0).getId()).isEqualTo(savedUser2.getId());
    }

    @Test
    void removeFriend_ShouldRemoveFriendship() {
        User savedUser1 = userRepository.save(testUser);
        User savedUser2 = userRepository.save(testUser2);

        userRepository.addFriend(savedUser1.getId(), savedUser2.getId());
        userRepository.removeFriend(savedUser1.getId(), savedUser2.getId());

        List<User> friends = userRepository.getFriends(savedUser1.getId());
        assertThat(friends).isEmpty();
    }

    @Test
    void getFriends_ShouldReturnOnlyFriends() {
        User savedUser1 = userRepository.save(testUser);
        User savedUser2 = userRepository.save(testUser2);

        User user3 = new User();
        user3.setEmail("user3@test.com");
        user3.setLogin("user3");
        user3.setName("User Three");
        user3.setBirthday(LocalDate.of(1992, 3, 3));
        User savedUser3 = userRepository.save(user3);

        userRepository.addFriend(savedUser1.getId(), savedUser2.getId());

        List<User> friends = userRepository.getFriends(savedUser1.getId());

        assertThat(friends).hasSize(1);
        assertThat(friends.get(0).getId()).isEqualTo(savedUser2.getId());
        assertThat(friends.get(0).getEmail()).isEqualTo("user2@test.com");
    }

    @Test
    void getFriends_WhenNoFriends_ShouldReturnEmptyList() {
        User savedUser1 = userRepository.save(testUser);

        List<User> friends = userRepository.getFriends(savedUser1.getId());

        assertThat(friends).isEmpty();
    }

    @Test
    void getCommonFriends_ShouldReturnIntersection() {
        User savedUser1 = userRepository.save(testUser);
        User savedUser2 = userRepository.save(testUser2);

        User user3 = new User();
        user3.setEmail("user3@test.com");
        user3.setLogin("user3");
        user3.setName("User Three");
        user3.setBirthday(LocalDate.of(1992, 3, 3));
        User savedUser3 = userRepository.save(user3);

        userRepository.addFriend(savedUser1.getId(), savedUser3.getId());
        userRepository.addFriend(savedUser2.getId(), savedUser3.getId());

        List<User> commonFriends = userRepository.getCommonFriends(savedUser1.getId(), savedUser2.getId());

        assertThat(commonFriends).hasSize(1);
        assertThat(commonFriends.get(0).getId()).isEqualTo(savedUser3.getId());
    }

    @Test
    void getCommonFriends_WhenNoCommon_ShouldReturnEmptyList() {
        User savedUser1 = userRepository.save(testUser);
        User savedUser2 = userRepository.save(testUser2);

        User user3 = new User();
        user3.setEmail("user3@test.com");
        user3.setLogin("user3");
        user3.setName("User Three");
        user3.setBirthday(LocalDate.of(1992, 3, 3));
        User savedUser3 = userRepository.save(user3);

        userRepository.addFriend(savedUser1.getId(), savedUser3.getId());

        List<User> commonFriends = userRepository.getCommonFriends(savedUser1.getId(), savedUser2.getId());

        assertThat(commonFriends).isEmpty();
    }

    @Test
    void save_WithDuplicateEmail_ShouldThrowException() {
        userRepository.save(testUser);

        User duplicateUser = new User();
        duplicateUser.setEmail("user1@test.com");
        duplicateUser.setLogin("user3");
        duplicateUser.setName("User Three");
        duplicateUser.setBirthday(LocalDate.of(1992, 3, 3));

        assertThrows(Exception.class, () -> userRepository.save(duplicateUser));
    }

    @Test
    void addFriend_WhenAlreadyFriends_ShouldNotDuplicate() {
        User savedUser1 = userRepository.save(testUser);
        User savedUser2 = userRepository.save(testUser2);

        userRepository.addFriend(savedUser1.getId(), savedUser2.getId());
        userRepository.addFriend(savedUser1.getId(), savedUser2.getId());

        List<User> friends = userRepository.getFriends(savedUser1.getId());
        assertThat(friends).hasSize(1);
    }

    @Test
    void removeFriend_WhenNotFriends_ShouldDoNothing() {
        User savedUser1 = userRepository.save(testUser);
        User savedUser2 = userRepository.save(testUser2);

        userRepository.removeFriend(savedUser1.getId(), savedUser2.getId());

        List<User> friends = userRepository.getFriends(savedUser1.getId());
        assertThat(friends).isEmpty();
    }

    @Test
    void findById_ShouldReturnUserWithCorrectFields() {
        User savedUser = userRepository.save(testUser);

        Optional<User> found = userRepository.findById(savedUser.getId());

        assertThat(found).isPresent();
        User user = found.get();
        assertThat(user.getId()).isPositive();
        assertThat(user.getEmail()).isEqualTo("user1@test.com");
        assertThat(user.getLogin()).isEqualTo("user1");
        assertThat(user.getName()).isEqualTo("User One");
        assertThat(user.getBirthday()).isEqualTo(LocalDate.of(1990, 1, 1));
    }
}
