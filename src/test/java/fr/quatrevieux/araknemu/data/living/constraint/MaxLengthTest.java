package fr.quatrevieux.araknemu.data.living.constraint;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MaxLengthTest {
    class Entity {
        String value;

        public Entity(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @Test
    void check() {
        MaxLength<Entity, Object> maxLength = new MaxLength<>(new Object(), Entity::getValue, 3);

        assertTrue(maxLength.check(new Entity("abc")));
        assertFalse(maxLength.check(new Entity("abcdef")));
    }
}
