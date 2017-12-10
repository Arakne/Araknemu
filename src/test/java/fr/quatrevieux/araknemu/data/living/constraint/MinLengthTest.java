package fr.quatrevieux.araknemu.data.living.constraint;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MinLengthTest {
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
        MinLength<Entity, Object> maxLength = new MinLength<>(new Object(), Entity::getValue, 3);

        assertFalse(maxLength.check(new Entity("ab")));
        assertTrue(maxLength.check(new Entity("abcdef")));
    }
}
