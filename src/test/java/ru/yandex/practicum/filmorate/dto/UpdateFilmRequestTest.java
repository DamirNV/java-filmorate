package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UpdateFilmRequestTest {

    private Validator validator;
    private UpdateFilmRequest request;
    private Mpa mpa;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        mpa = new Mpa();
        mpa.setId(1);

        request = new UpdateFilmRequest();
        request.setId(1L);
        request.setName("Updated Film");
        request.setDescription("Updated description");
        request.setReleaseDate(LocalDate.of(2000, 1, 1));
        request.setDuration(120);
        request.setMpa(mpa);
    }

    @Test
    void validRequestShouldHaveNoViolations() {
        Set<ConstraintViolation<UpdateFilmRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void tooLongDescriptionShouldFailValidation() {
        request.setDescription("A".repeat(201));
        Set<ConstraintViolation<UpdateFilmRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.iterator().next().getMessage().contains("Описание не может быть длиннее 200 символов"));
    }

    @Test
    void exactly200CharDescriptionShouldPassValidation() {
        request.setDescription("A".repeat(200));
        Set<ConstraintViolation<UpdateFilmRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void negativeDurationShouldFailValidation() {
        request.setDuration(-10);
        Set<ConstraintViolation<UpdateFilmRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.iterator().next().getMessage().contains("Продолжительность должна быть положительной"));
    }

    @Test
    void zeroDurationShouldFailValidation() {
        request.setDuration(0);
        Set<ConstraintViolation<UpdateFilmRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void tooOldReleaseDateShouldFailValidation() {
        request.setReleaseDate(LocalDate.of(1895, 12, 27));
        Set<ConstraintViolation<UpdateFilmRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.iterator().next().getMessage().contains("Дата релиза не может быть раньше 28 декабря 1895 года"));
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
    void hasDescription_ShouldReturnTrueWhenDescriptionPresent() {
        assertTrue(request.hasDescription());
        request.setDescription(null);
        assertFalse(request.hasDescription());
        request.setDescription(" ");
        assertFalse(request.hasDescription());
    }

    @Test
    void hasReleaseDate_ShouldReturnTrueWhenReleaseDatePresent() {
        assertTrue(request.hasReleaseDate());
        request.setReleaseDate(null);
        assertFalse(request.hasReleaseDate());
    }

    @Test
    void hasDuration_ShouldReturnTrueWhenDurationPresent() {
        assertTrue(request.hasDuration());
        request.setDuration(null);
        assertFalse(request.hasDuration());
    }

    @Test
    void hasMpa_ShouldReturnTrueWhenMpaPresent() {
        assertTrue(request.hasMpa());
        request.setMpa(null);
        assertFalse(request.hasMpa());
    }

    @Test
    void hasGenres_ShouldReturnTrueWhenGenresPresentAndNotEmpty() {
        request.setGenres(Set.of(new CreateFilmRequest.GenreId()));
        assertTrue(request.hasGenres());
        request.setGenres(null);
        assertFalse(request.hasGenres());
        request.setGenres(Set.of());
        assertFalse(request.hasGenres());
    }
}
