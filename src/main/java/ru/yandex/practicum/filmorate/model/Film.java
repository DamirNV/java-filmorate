package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.validator.ReleaseDate;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class Film {
    private int id;
    private Set<Integer> likes = new HashSet<>();

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание не может быть длиннее 200 символов")
    private String description;

    @NotNull(message = "Дата релиза обязательна")
    @ReleaseDate
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность должна быть положительной")
    private int duration;

    private Set<Genre> genres = new LinkedHashSet<>();
}