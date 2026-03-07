package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.repositories.MpaRepository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaRepository mpaRepository;

    public List<Mpa> getAllMpa() {
        log.info("Получение всех рейтингов MPA");
        return mpaRepository.findAll();
    }

    public Mpa getMpaById(int id) {
        log.info("Получение рейтинга MPA по id: {}", id);
        return mpaRepository.findById(id);
    }
}