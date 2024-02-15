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

package fr.quatrevieux.araknemu.game.player.sprite;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SpriteSizeTest {
    @Test
    void values() {
        SpriteSize size = new SpriteSize(150, 120);

        assertEquals(150, size.x());
        assertEquals(120, size.y());
        assertEquals("150x120", size.toString());
    }

    @Test
    void defaultSize() {
        assertEquals(100, SpriteSize.DEFAULT.x());
        assertEquals(100, SpriteSize.DEFAULT.y());
        assertEquals("100x100", SpriteSize.DEFAULT.toString());
    }

    @Test
    void equalsAndHash() {
        SpriteSize size = new SpriteSize(150, 120);

        assertEquals(size, size);
        assertEquals(size, new SpriteSize(150, 120));
        assertNotEquals(size, new SpriteSize(150, 130));
        assertNotEquals(size, new SpriteSize(140, 120));
        assertNotEquals(size, new Object());
        assertFalse(size.equals(null));

        assertEquals(size.hashCode(), size.hashCode());
        assertEquals(size.hashCode(), new SpriteSize(150, 120).hashCode());
        assertNotEquals(size.hashCode(), new SpriteSize(150, 130).hashCode());
        assertNotEquals(size.hashCode(), new SpriteSize(140, 120).hashCode());
    }
}
