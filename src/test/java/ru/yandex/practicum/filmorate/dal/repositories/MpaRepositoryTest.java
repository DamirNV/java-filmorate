package ru.yandex.practicum.filmorate.dal.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(MpaRepository.class)
@Sql(scripts = {"/schema.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class MpaRepositoryTest {

    @Autowired
    private MpaRepository mpaRepository;

    @Test
    void findAll_ShouldReturnAllRatings() {
        List<Mpa> ratings = mpaRepository.findAll();

        assertThat(ratings).hasSize(5);

        assertThat(ratings.get(0).getId()).isEqualTo(1);
        assertThat(ratings.get(0).getName()).isEqualTo("G");

        assertThat(ratings.get(1).getId()).isEqualTo(2);
        assertThat(ratings.get(1).getName()).isEqualTo("PG");

        assertThat(ratings.get(2).getId()).isEqualTo(3);
        assertThat(ratings.get(2).getName()).isEqualTo("PG-13");

        assertThat(ratings.get(3).getId()).isEqualTo(4);
        assertThat(ratings.get(3).getName()).isEqualTo("R");

        assertThat(ratings.get(4).getId()).isEqualTo(5);
        assertThat(ratings.get(4).getName()).isEqualTo("NC-17");
    }

    @Test
    void findById_ShouldReturnRating() {
        Optional<Mpa> g = mpaRepository.findById(1);
        assertThat(g).isPresent();
        assertThat(g.get().getId()).isEqualTo(1);
        assertThat(g.get().getName()).isEqualTo("G");

        Optional<Mpa> pg = mpaRepository.findById(2);
        assertThat(pg).isPresent();
        assertThat(pg.get().getId()).isEqualTo(2);
        assertThat(pg.get().getName()).isEqualTo("PG");

        Optional<Mpa> pg13 = mpaRepository.findById(3);
        assertThat(pg13).isPresent();
        assertThat(pg13.get().getId()).isEqualTo(3);
        assertThat(pg13.get().getName()).isEqualTo("PG-13");

        Optional<Mpa> r = mpaRepository.findById(4);
        assertThat(r).isPresent();
        assertThat(r.get().getId()).isEqualTo(4);
        assertThat(r.get().getName()).isEqualTo("R");

        Optional<Mpa> nc17 = mpaRepository.findById(5);
        assertThat(nc17).isPresent();
        assertThat(nc17.get().getId()).isEqualTo(5);
        assertThat(nc17.get().getName()).isEqualTo("NC-17");
    }

    @Test
    void findById_WhenNotFound_ShouldReturnEmpty() {
        Optional<Mpa> found = mpaRepository.findById(999);
        assertThat(found).isEmpty();
    }

    @Test
    void findAll_ShouldReturnRatingsInCorrectOrder() {
        List<Mpa> ratings = mpaRepository.findAll();

        assertThat(ratings).hasSize(5);
        assertThat(ratings).extracting(Mpa::getId)
                .containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    void findById_ShouldReturnRatingWithBothIdAndName() {
        Optional<Mpa> rating = mpaRepository.findById(3);

        assertThat(rating).isPresent();
        assertThat(rating.get().getId()).isEqualTo(3);
        assertThat(rating.get().getName()).isNotBlank();
    }

    @Test
    void findAll_ShouldNotReturnNullNames() {
        List<Mpa> ratings = mpaRepository.findAll();

        assertThat(ratings).allMatch(rating -> rating.getName() != null);
        assertThat(ratings).allMatch(rating -> !rating.getName().isBlank());
    }

    @Test
    void findById_ShouldReturnCorrectNameForEachId() {
        Optional<Mpa> rating1 = mpaRepository.findById(1);
        assertThat(rating1).isPresent();
        assertThat(rating1.get().getName()).isEqualTo("G");

        Optional<Mpa> rating2 = mpaRepository.findById(2);
        assertThat(rating2).isPresent();
        assertThat(rating2.get().getName()).isEqualTo("PG");

        Optional<Mpa> rating3 = mpaRepository.findById(3);
        assertThat(rating3).isPresent();
        assertThat(rating3.get().getName()).isEqualTo("PG-13");

        Optional<Mpa> rating4 = mpaRepository.findById(4);
        assertThat(rating4).isPresent();
        assertThat(rating4.get().getName()).isEqualTo("R");

        Optional<Mpa> rating5 = mpaRepository.findById(5);
        assertThat(rating5).isPresent();
        assertThat(rating5.get().getName()).isEqualTo("NC-17");
    }
}
