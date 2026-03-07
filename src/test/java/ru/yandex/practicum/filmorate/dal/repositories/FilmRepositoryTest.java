package ru.yandex.practicum.filmorate.dal.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import({FilmRepository.class, FilmRowMapper.class})
@Sql(scripts = {"/schema.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class FilmRepositoryTest {

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Film testFilm;
    private Mpa testMpa;
    private Genre testGenre1;
    private Genre testGenre2;
    private Set<Genre> testGenres;

    @BeforeEach
    void setUp() {
        testMpa = new Mpa();
        testMpa.setId(1);
        testMpa.setName("G");

        testGenre1 = new Genre();
        testGenre1.setId(1);
        testGenre1.setName("Комедия");

        testGenre2 = new Genre();
        testGenre2.setId(2);
        testGenre2.setName("Драма");

        testGenres = new LinkedHashSet<>();
        testGenres.add(testGenre1);
        testGenres.add(testGenre2);

        testFilm = new Film();
        testFilm.setName("Test Film");
        testFilm.setDescription("Test Description");
        testFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        testFilm.setDuration(120);
        testFilm.setMpa(testMpa);
        testFilm.setGenres(testGenres);

        jdbcTemplate.execute("INSERT INTO users (email, login, name, birthday) VALUES ('likeuser@test.com', 'likeuser', 'Like User', '1990-01-01')");
    }

    @Test
    void save_ShouldCreateFilmAndReturnWithId() {
        Film savedFilm = filmRepository.save(testFilm);

        assertThat(savedFilm.getId()).isPositive();
        assertThat(savedFilm.getName()).isEqualTo("Test Film");
        assertThat(savedFilm.getMpa().getId()).isEqualTo(1);
        assertThat(savedFilm.getGenres()).hasSize(2);

        Optional<Film> retrieved = filmRepository.findById(savedFilm.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getName()).isEqualTo("Test Film");
        assertThat(retrieved.get().getGenres()).hasSize(2);
    }

    @Test
    void findById_ShouldReturnFilm() {
        Film savedFilm = filmRepository.save(testFilm);

        Optional<Film> found = filmRepository.findById(savedFilm.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Film");
        assertThat(found.get().getMpa()).isNotNull();
        assertThat(found.get().getGenres()).hasSize(2);
    }

    @Test
    void findById_WhenNotFound_ShouldReturnEmpty() {
        Optional<Film> found = filmRepository.findById(999);

        assertThat(found).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllFilms() {
        filmRepository.save(testFilm);

        Film secondFilm = new Film();
        secondFilm.setName("Second Film");
        secondFilm.setDescription("Second Description");
        secondFilm.setReleaseDate(LocalDate.of(2001, 1, 1));
        secondFilm.setDuration(130);
        secondFilm.setMpa(testMpa);
        secondFilm.setGenres(Set.of(testGenre1));

        filmRepository.save(secondFilm);

        List<Film> films = filmRepository.findAll();

        assertThat(films).hasSize(2);
        assertThat(films).extracting(Film::getName)
                .containsExactlyInAnyOrder("Test Film", "Second Film");
    }

    @Test
    void update_ShouldUpdateFilm() {
        Film savedFilm = filmRepository.save(testFilm);
        savedFilm.setName("Updated Name");
        savedFilm.setDescription("Updated Description");
        savedFilm.setDuration(150);

        Film updatedFilm = filmRepository.update(savedFilm);

        assertThat(updatedFilm.getName()).isEqualTo("Updated Name");
        assertThat(updatedFilm.getDescription()).isEqualTo("Updated Description");
        assertThat(updatedFilm.getDuration()).isEqualTo(150);

        Optional<Film> retrieved = filmRepository.findById(savedFilm.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getName()).isEqualTo("Updated Name");
    }

    @Test
    void update_ShouldUpdateGenres() {
        Film savedFilm = filmRepository.save(testFilm);
        savedFilm.setGenres(Set.of(testGenre1));

        Film updatedFilm = filmRepository.update(savedFilm);

        assertThat(updatedFilm.getGenres()).hasSize(1);
        assertThat(updatedFilm.getGenres()).extracting(Genre::getId).containsExactly(1);

        Optional<Film> retrieved = filmRepository.findById(savedFilm.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getGenres()).hasSize(1);
    }

    @Test
    void delete_ShouldRemoveFilm() {
        Film savedFilm = filmRepository.save(testFilm);

        boolean deleted = filmRepository.delete(savedFilm.getId());

        assertTrue(deleted);
        Optional<Film> retrieved = filmRepository.findById(savedFilm.getId());
        assertThat(retrieved).isEmpty();
    }

    @Test
    void delete_WhenNotFound_ShouldReturnFalse() {
        boolean deleted = filmRepository.delete(999);

        assertFalse(deleted);
    }

    @Test
    void addLike_ShouldAddLike() {
        Film savedFilm = filmRepository.save(testFilm);

        filmRepository.addLike(savedFilm.getId(), 1);
        filmRepository.addLike(savedFilm.getId(), 2);

        Optional<Film> retrieved = filmRepository.findById(savedFilm.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getLikes()).hasSize(2);
        assertThat(retrieved.get().getLikes()).contains(1, 2);
    }

    @Test
    void removeLike_ShouldRemoveLike() {
        Film savedFilm = filmRepository.save(testFilm);
        filmRepository.addLike(savedFilm.getId(), 1);
        filmRepository.addLike(savedFilm.getId(), 2);

        filmRepository.removeLike(savedFilm.getId(), 1);

        Optional<Film> retrieved = filmRepository.findById(savedFilm.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getLikes()).hasSize(1);
        assertThat(retrieved.get().getLikes()).contains(2);
    }

    @Test
    void removeLike_WhenNotLiked_ShouldDoNothing() {
        Film savedFilm = filmRepository.save(testFilm);

        filmRepository.removeLike(savedFilm.getId(), 1);

        Optional<Film> retrieved = filmRepository.findById(savedFilm.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getLikes()).isEmpty();
    }

    @Test
    void save_WithoutGenres_ShouldCreateFilm() {
        testFilm.setGenres(null);
        Film savedFilm = filmRepository.save(testFilm);

        assertThat(savedFilm.getId()).isPositive();
        assertThat(savedFilm.getGenres()).isEmpty();

        Optional<Film> retrieved = filmRepository.findById(savedFilm.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getGenres()).isEmpty();
    }

    @Test
    void save_WithDuplicateGenres_ShouldStoreOnlyUnique() {
        Set<Genre> duplicateGenres = new LinkedHashSet<>();
        duplicateGenres.add(testGenre1);
        duplicateGenres.add(testGenre1);
        testFilm.setGenres(duplicateGenres);

        Film savedFilm = filmRepository.save(testFilm);

        assertThat(savedFilm.getGenres()).hasSize(1);

        Optional<Film> retrieved = filmRepository.findById(savedFilm.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getGenres()).hasSize(1);
    }

    @Test
    void findById_ShouldLoadLikes() {
        Film savedFilm = filmRepository.save(testFilm);
        filmRepository.addLike(savedFilm.getId(), 1);
        filmRepository.addLike(savedFilm.getId(), 2);

        Optional<Film> retrieved = filmRepository.findById(savedFilm.getId());

        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getLikes()).hasSize(2);
    }

    @Test
    void findAll_ShouldLoadLikesAndGenresForAllFilms() {
        Film savedFilm1 = filmRepository.save(testFilm);

        Film film2 = new Film();
        film2.setName("Film 2");
        film2.setDescription("Description 2");
        film2.setReleaseDate(LocalDate.of(2001, 1, 1));
        film2.setDuration(130);
        film2.setMpa(testMpa);
        film2.setGenres(Set.of(testGenre2));
        Film savedFilm2 = filmRepository.save(film2);

        filmRepository.addLike(savedFilm1.getId(), 1);
        filmRepository.addLike(savedFilm2.getId(), 2);
        filmRepository.addLike(savedFilm2.getId(), 3);

        List<Film> films = filmRepository.findAll();

        assertThat(films).hasSize(2);

        Film foundFilm1 = films.stream().filter(f -> f.getId() == savedFilm1.getId()).findFirst().orElseThrow();
        assertThat(foundFilm1.getGenres()).hasSize(2);
        assertThat(foundFilm1.getLikes()).hasSize(1);

        Film foundFilm2 = films.stream().filter(f -> f.getId() == savedFilm2.getId()).findFirst().orElseThrow();
        assertThat(foundFilm2.getGenres()).hasSize(1);
        assertThat(foundFilm2.getLikes()).hasSize(2);
    }
}