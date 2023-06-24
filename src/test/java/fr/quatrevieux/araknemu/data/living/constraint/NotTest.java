/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.data.living.constraint;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

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