package fr.quatrevieux.araknemu.data.living.constraint;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValueCheckTest {
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
        ValueCheck.Checker<String> checker = Mockito.mock(ValueCheck.Checker.class);

        ValueCheck<Entity, Object, String> maxLength = new ValueCheck<>(new Object(), Entity::getValue, checker);

        Mockito.when(checker.check("abcd")).thenReturn(true);
        assertTrue(maxLength.check(new Entity("abcd")));

        Mockito.when(checker.check("abcd")).thenReturn(false);
        assertFalse(maxLength.check(new Entity("abcd")));
    }
}
