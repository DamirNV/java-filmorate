package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.film.CreateFilmRequest;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CreateFilmRequestTest {

    private Validator validator;
    private CreateFilmRequest request;
    private Mpa mpa;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        mpa = new Mpa();
        mpa.setId(1);

        request = new CreateFilmRequest();
        request.setName("Valid Film");
        request.setDescription("Valid description");
        request.setReleaseDate(LocalDate.of(2000, 1, 1));
        request.setDuration(120);
        request.setMpa(mpa);
    }

    @Test
    void validRequestShouldHaveNoViolations() {
        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void blankNameShouldFailValidation() {
        request.setName(" ");
        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void nullNameShouldFailValidation() {
        request.setName(null);
        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void tooLongDescriptionShouldFailValidation() {
        request.setDescription("A".repeat(201));
        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void exactly200CharDescriptionShouldPassValidation() {
        request.setDescription("A".repeat(200));
        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void nullReleaseDateShouldFailValidation() {
        request.setReleaseDate(null);
        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void tooOldReleaseDateShouldFailValidation() {
        request.setReleaseDate(LocalDate.of(1895, 12, 27));
        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void minReleaseDateShouldPassValidation() {
        request.setReleaseDate(LocalDate.of(1895, 12, 28));
        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void negativeDurationShouldFailValidation() {
        request.setDuration(-10);
        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void zeroDurationShouldFailValidation() {
        request.setDuration(0);
        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void nullMpaShouldFailValidation() {
        request.setMpa(null);
        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void nullGenreIdInSetShouldFailValidation() {
        CreateFilmRequest.GenreId nullGenreId = new CreateFilmRequest.GenreId();
        nullGenreId.setId(null);
        request.setGenres(Set.of(nullGenreId));

        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }
}