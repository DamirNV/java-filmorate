package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private int id;
    private Set<Friendship> friendships = new HashSet<>();
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
}