package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.validator.ReleaseDate;

import java.time.LocalDate;
import java.util.Set;

@Data
public class CreateFilmRequest {

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание не может быть длиннее 200 символов")
    private String description;

    @NotNull(message = "Дата релиза обязательна")
    @ReleaseDate
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность должна быть положительной")
    private int duration;

    @NotNull(message = "Рейтинг MPA должен быть указан")
    private Mpa mpa;

    private Set<GenreId> genres;

    @Data
    public static class GenreId {
        @NotNull
        private Integer id;
    }
}