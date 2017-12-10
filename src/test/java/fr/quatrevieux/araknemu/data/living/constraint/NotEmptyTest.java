package fr.quatrevieux.araknemu.data.living.constraint;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotEmptyTest {
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
        NotEmpty<Entity, Object> maxLength = new NotEmpty<>(new Object(), Entity::getValue);

        assertTrue(maxLength.check(new Entity("ab")));
        assertFalse(maxLength.check(new Entity(null)));
        assertFalse(maxLength.check(new Entity("")));
    }
}
