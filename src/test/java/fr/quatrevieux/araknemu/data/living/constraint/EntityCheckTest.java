package fr.quatrevieux.araknemu.data.living.constraint;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EntityCheckTest {
    class Entity {
    }

    @Test
    void check() {
        Object error = new Object();
        Entity e = new Entity();

        EntityCheck<Entity, Object> check = new EntityCheck<>(error, entity -> {
            assertSame(e, entity);

            return true;
        });

        assertTrue(check.check(e));
        assertSame(error, check.error());
    }
}
