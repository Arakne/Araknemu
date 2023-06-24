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

package fr.quatrevieux.araknemu.data.living.transformer;

import fr.quatrevieux.araknemu.common.account.Permission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PermissionsTransformerTest {
    private PermissionsTransformer transformer;

    @BeforeEach
    public void setUp() throws Exception {
        transformer = new PermissionsTransformer();
    }

    @Test
    void serializeNull() {
        assertEquals(0, transformer.serialize(null));
    }

    @Test
    void serializeEmpty() {
        assertEquals(0, transformer.serialize(EnumSet.noneOf(Permission.class)));
    }

    @Test
    void serializeOne() {
        assertEquals(2, transformer.serialize(EnumSet.of(Permission.SUPER_ADMIN)));
    }

    @Test
    void serializeAll() {
        assertEquals(31, transformer.serialize(EnumSet.allOf(Permission.class)));
    }

    @Test
    void unserializeZero() {
        assertEquals(
            EnumSet.noneOf(Permission.class),
            transformer.unserialize(0)
        );
    }

    @Test
    void unserializeOne() {
        assertEquals(
            EnumSet.of(Permission.SUPER_ADMIN),
            transformer.unserialize(2)
        );
    }

    @Test
    void unserializeTwo() {
        assertEquals(
            EnumSet.of(Permission.ACCESS, Permission.SUPER_ADMIN),
            transformer.unserialize(3)
        );
    }

    @Test
    void unserializeUndefined() {
        assertEquals(
            EnumSet.noneOf(Permission.class),
            transformer.unserialize(0x10000000)
        );
    }
}
