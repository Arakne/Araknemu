package fr.quatrevieux.araknemu.data.living.constraint;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MaxTest {
    class Entity {
        int value;

        public Entity(int value) {
            this.value = value;
        }
    }

    @Test
    void check() {
        Max<Entity, Object, Integer> max = new Max<>(new Object(), entity -> entity.value, 8);

        assertTrue(max.check(new Entity(5)));
        assertFalse(max.check(new Entity(15)));
    }
}
