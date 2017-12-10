package fr.quatrevieux.araknemu.data.living.constraint;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MustTest {
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
        Must<Entity, Object> must = new Must<>(new EntityConstraint[] {
            new NotEmpty<>(new Object(), Entity::getValue),
            new MaxLength<>(new Object(), Entity::getValue, 5)
        });

        assertFalse(must.check(new Entity("")));
        assertFalse(must.check(new Entity("azertyuiop")));
        assertTrue(must.check(new Entity("123")));
    }

    @Test
    void error() {
        Must<Entity, Integer> must = new Must<>(new EntityConstraint[] {
            new NotEmpty<>(1, Entity::getValue),
            new MaxLength<>(2, Entity::getValue, 5)
        });

        must.check(new Entity(""));
        assertEquals(1, (int) must.error());

        must.check(new Entity("azertyuiop"));
        assertEquals(2, (int) must.error());

        must.check(new Entity("123"));
        assertNull(must.error());
    }
}
