package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MpaTest {

    @Test
    void equals_SameId_ShouldReturnTrue() {
        Mpa mpa1 = new Mpa();
        mpa1.setId(1);
        mpa1.setName("G");

        Mpa mpa2 = new Mpa();
        mpa2.setId(1);
        mpa2.setName("PG-13");

        assertEquals(mpa1, mpa2);
    }

    @Test
    void equals_DifferentId_ShouldReturnFalse() {
        Mpa mpa1 = new Mpa();
        mpa1.setId(1);
        mpa1.setName("G");

        Mpa mpa2 = new Mpa();
        mpa2.setId(2);
        mpa2.setName("G");

        assertNotEquals(mpa1, mpa2);
    }

    @Test
    void equals_SameObject_ShouldReturnTrue() {
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        assertEquals(mpa, mpa);
    }

    @Test
    void equals_Null_ShouldReturnFalse() {
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        assertNotEquals(null, mpa);
    }

    @Test
    void equals_DifferentClass_ShouldReturnFalse() {
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        String notAnMpa = "not an mpa";
        assertNotEquals(mpa, notAnMpa);
    }

    @Test
    void hashCode_SameId_ShouldBeEqual() {
        Mpa mpa1 = new Mpa();
        mpa1.setId(1);
        mpa1.setName("G");

        Mpa mpa2 = new Mpa();
        mpa2.setId(1);
        mpa2.setName("PG-13");

        assertEquals(mpa1.hashCode(), mpa2.hashCode());
    }

    @Test
    void hashCode_DifferentId_ShouldBeDifferent() {
        Mpa mpa1 = new Mpa();
        mpa1.setId(1);
        mpa1.setName("G");

        Mpa mpa2 = new Mpa();
        mpa2.setId(2);
        mpa2.setName("G");

        assertNotEquals(mpa1.hashCode(), mpa2.hashCode());
    }

    @Test
    void constructorAndGetters_ShouldWork() {
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        assertEquals(1, mpa.getId());
        assertEquals("G", mpa.getName());
    }

    @Test
    void toString_ShouldNotBeEmpty() {
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        assertNotNull(mpa.toString());
        assertFalse(mpa.toString().isEmpty());
    }
}
