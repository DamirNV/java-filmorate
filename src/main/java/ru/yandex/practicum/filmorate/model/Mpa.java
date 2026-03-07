package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import java.util.Objects;

@Data
public class Mpa {
    private int id;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mpa mpa = (Mpa) o;
        return id == mpa.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}