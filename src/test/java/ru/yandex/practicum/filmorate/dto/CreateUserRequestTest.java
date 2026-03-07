package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.user.CreateUserRequest;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CreateUserRequestTest {

    private Validator validator;
    private CreateUserRequest request;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        request = new CreateUserRequest();
        request.setEmail("test@example.com");
        request.setLogin("testlogin");
        request.setName("Test User");
        request.setBirthday(LocalDate.of(2000, 1, 1));
    }

    @Test
    void validRequestShouldHaveNoViolations() {
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void blankEmailShouldFailValidation() {
        request.setEmail(" ");
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.iterator().next().getMessage().contains("Электронная почта не может быть пустой"));
    }

    @Test
    void nullEmailShouldFailValidation() {
        request.setEmail(null);
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void invalidEmailShouldFailValidation() {
        request.setEmail("invalid-email");
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.iterator().next().getMessage().contains("Электронная почта должна быть валидной"));
    }

    @Test
    void blankLoginShouldFailValidation() {
        request.setLogin(" ");
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.iterator().next().getMessage().contains("Логин не может быть пустым"));
    }

    @Test
    void nullLoginShouldFailValidation() {
        request.setLogin(null);
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void loginWithSpacesShouldFailValidation() {
        request.setLogin("login with spaces");
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.iterator().next().getMessage().contains("Логин не может содержать пробелы"));
    }

    @Test
    void futureBirthdayShouldFailValidation() {
        request.setBirthday(LocalDate.now().plusDays(1));
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.iterator().next().getMessage().contains("Дата рождения не может быть в будущем"));
    }

    @Test
    void nullBirthdayShouldPassValidation() {
        request.setBirthday(null);
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void nullNameShouldPassValidation() {
        request.setName(null);
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
}