package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class ReleaseDateValidator implements ConstraintValidator<ReleaseDate, LocalDate> {

    private LocalDate minDate;

    @Override
    public void initialize(ReleaseDate constraintAnnotation) {
        this.minDate = LocalDate.parse(constraintAnnotation.minDate());
    }

    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext context) {
        if (releaseDate == null) {
            return true;
        }
        return !releaseDate.isBefore(minDate);
    }
}