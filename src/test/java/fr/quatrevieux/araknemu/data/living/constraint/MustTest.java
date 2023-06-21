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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
