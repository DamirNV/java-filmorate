package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UpdateUserRequestTest {

    private Validator validator;
    private UpdateUserRequest request;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        request = new UpdateUserRequest();
        request.setId(1L);
        request.setEmail("test@example.com");
        request.setLogin("testlogin");
        request.setName("Test User");
        request.setBirthday(LocalDate.of(2000, 1, 1));
    }

    @Test
    void validRequestShouldHaveNoViolations() {
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidEmailShouldFailValidation() {
        request.setEmail("invalid-email");
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.iterator().next().getMessage().contains("Электронная почта должна быть валидной"));
    }

    @Test
    void loginWithSpacesShouldFailValidation() {
        request.setLogin("login with spaces");
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.iterator().next().getMessage().contains("Логин не может содержать пробелы"));
    }

    @Test
    void futureBirthdayShouldFailValidation() {
        request.setBirthday(LocalDate.now().plusDays(1));
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.iterator().next().getMessage().contains("Дата рождения не может быть в будущем"));
    }

    @Test
    void hasEmail_ShouldReturnTrueWhenEmailPresent() {
        assertTrue(request.hasEmail());
        request.setEmail(null);
        assertFalse(request.hasEmail());
        request.setEmail(" ");
        assertFalse(request.hasEmail());
    }

    @Test
    void hasLogin_ShouldReturnTrueWhenLoginPresent() {
        assertTrue(request.hasLogin());
        request.setLogin(null);
        assertFalse(request.hasLogin());
        request.setLogin(" ");
        assertFalse(request.hasLogin());
    }

    @Test
    void hasName_ShouldReturnTrueWhenNamePresent() {
        assertTrue(request.hasName());
        request.setName(null);
        assertFalse(request.hasName());
        request.setName(" ");
        assertFalse(request.hasName());
    }

    @Test
    void hasBirthday_ShouldReturnTrueWhenBirthdayPresent() {
        assertTrue(request.hasBirthday());
        request.setBirthday(null);
        assertFalse(request.hasBirthday());
    }
}