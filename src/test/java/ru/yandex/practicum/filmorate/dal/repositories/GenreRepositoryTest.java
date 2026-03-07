package ru.yandex.practicum.filmorate.dal.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(GenreRepository.class)
@Sql(scripts = {"/schema.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class GenreRepositoryTest {

    @Autowired
    private GenreRepository genreRepository;

    @Test
    void findAll_ShouldReturnAllGenres() {
        List<Genre> genres = genreRepository.findAll();

        assertThat(genres).hasSize(6);

        assertThat(genres.get(0).getId()).isEqualTo(1);
        assertThat(genres.get(0).getName()).isEqualTo("Комедия");

        assertThat(genres.get(1).getId()).isEqualTo(2);
        assertThat(genres.get(1).getName()).isEqualTo("Драма");

        assertThat(genres.get(2).getId()).isEqualTo(3);
        assertThat(genres.get(2).getName()).isEqualTo("Мультфильм");

        assertThat(genres.get(3).getId()).isEqualTo(4);
        assertThat(genres.get(3).getName()).isEqualTo("Триллер");

        assertThat(genres.get(4).getId()).isEqualTo(5);
        assertThat(genres.get(4).getName()).isEqualTo("Документальный");

        assertThat(genres.get(5).getId()).isEqualTo(6);
        assertThat(genres.get(5).getName()).isEqualTo("Боевик");
    }

    @Test
    void findById_ShouldReturnGenre() {
        Optional<Genre> comedy = genreRepository.findById(1);
        assertThat(comedy).isPresent();
        assertThat(comedy.get().getId()).isEqualTo(1);
        assertThat(comedy.get().getName()).isEqualTo("Комедия");

        Optional<Genre> drama = genreRepository.findById(2);
        assertThat(drama).isPresent();
        assertThat(drama.get().getId()).isEqualTo(2);
        assertThat(drama.get().getName()).isEqualTo("Драма");

        Optional<Genre> cartoon = genreRepository.findById(3);
        assertThat(cartoon).isPresent();
        assertThat(cartoon.get().getId()).isEqualTo(3);
        assertThat(cartoon.get().getName()).isEqualTo("Мультфильм");

        Optional<Genre> thriller = genreRepository.findById(4);
        assertThat(thriller).isPresent();
        assertThat(thriller.get().getId()).isEqualTo(4);
        assertThat(thriller.get().getName()).isEqualTo("Триллер");

        Optional<Genre> documentary = genreRepository.findById(5);
        assertThat(documentary).isPresent();
        assertThat(documentary.get().getId()).isEqualTo(5);
        assertThat(documentary.get().getName()).isEqualTo("Документальный");

        Optional<Genre> action = genreRepository.findById(6);
        assertThat(action).isPresent();
        assertThat(action.get().getId()).isEqualTo(6);
        assertThat(action.get().getName()).isEqualTo("Боевик");
    }

    @Test
    void findById_WhenNotFound_ShouldReturnEmpty() {
        Optional<Genre> found = genreRepository.findById(999);
        assertThat(found).isEmpty();
    }

    @Test
    void findAll_ShouldReturnGenresInCorrectOrder() {
        List<Genre> genres = genreRepository.findAll();

        assertThat(genres).hasSize(6);
        assertThat(genres).extracting(Genre::getId)
                .containsExactly(1, 2, 3, 4, 5, 6);
    }

    @Test
    void findById_ShouldReturnGenreWithBothIdAndName() {
        Optional<Genre> genre = genreRepository.findById(1);

        assertThat(genre).isPresent();
        assertThat(genre.get().getId()).isEqualTo(1);
        assertThat(genre.get().getName()).isNotBlank();
    }

    @Test
    void findAll_ShouldNotReturnNullNames() {
        List<Genre> genres = genreRepository.findAll();

        assertThat(genres).allMatch(genre -> genre.getName() != null);
        assertThat(genres).allMatch(genre -> !genre.getName().isBlank());
    }
}
