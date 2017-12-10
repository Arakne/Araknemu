package fr.quatrevieux.araknemu.data.living.constraint;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class NotTest {
    @Test
    void check() {
        Object entity = new Object();
        EntityConstraint constraint = Mockito.mock(EntityConstraint.class);
        Not not = new Not(constraint);

        Mockito.when(constraint.check(entity)).thenReturn(true);
        assertFalse(not.check(entity));

        Mockito.when(constraint.check(entity)).thenReturn(false);
        assertTrue(not.check(entity));
    }

    @Test
    void error() {
        Object error = new Object();

        EntityConstraint constraint = Mockito.mock(EntityConstraint.class);
        Not not = new Not(constraint);

        Mockito.when(constraint.error()).thenReturn(error);
        assertSame(error, not.error());
    }
}