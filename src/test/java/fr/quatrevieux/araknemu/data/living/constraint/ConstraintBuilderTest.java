package fr.quatrevieux.araknemu.data.living.constraint;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConstraintBuilderTest {
    class Entity {
        public int a;
        public String b;

        public Entity(int a, String b) {
            this.a = a;
            this.b = b;
        }

        public int getA() {
            return a;
        }

        public String getB() {
            return b;
        }
    }

    @Test
    void withOneConstraint() {
        ConstraintBuilder builder = new ConstraintBuilder();

        EntityConstraint constraint = builder.max(5).build();

        assertTrue(constraint instanceof Max);
    }

    @Test
    void withTwoConstraints() {
        ConstraintBuilder builder = new ConstraintBuilder();

        EntityConstraint constraint = builder.max(5).minLength(3).build();

        assertTrue(constraint instanceof Must);
    }

    @Test
    void functional() {
        ConstraintBuilder<Entity, String> builder = new ConstraintBuilder<>();

        builder
            .error("a")
            .value(Entity::getA)
            .max(12)

            .error("b")
            .value(Entity::getB)
            .minLength(3)
            .maxLength(5)
            .not(b -> b.regex("\\w+"))
        ;

        EntityConstraint<Entity, String> constraint = builder.build();

        assertFalse(constraint.check(new Entity(123, "")));
        assertEquals("a", constraint.error());

        assertFalse(constraint.check(new Entity(5, "abc")));
        assertEquals("b", constraint.error());

        assertTrue(constraint.check(new Entity(5, "####")));
        assertNull(constraint.error());
    }
}
