package fr.quatrevieux.araknemu.data.living.constraint;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegexTest {
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
        Regex<Entity, Object> maxLength = new Regex<>(new Object(), Entity::getValue, "\\w{4,10}");

        assertTrue(maxLength.check(new Entity("abcd")));
        assertFalse(maxLength.check(new Entity("###")));
    }
}
