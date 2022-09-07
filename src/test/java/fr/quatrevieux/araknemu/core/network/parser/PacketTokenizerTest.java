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
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.core.network.parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class PacketTokenizerTest {
    @Test
    void empty() {
        PacketTokenizer splitter = new PacketTokenizer("PK", "", ',');

        assertTrue(splitter.hasNext());
        assertEquals("", splitter.nextPart());

        assertFalse(splitter.hasNext());
        assertThrows(ParsePacketException.class, splitter::nextPart);

        try {
            splitter.nextPart();
            fail("ParsePacketException expected");
        } catch (ParsePacketException e) {
            assertEquals("PK", e.packet());
        }
    }

    @Test
    void withoutSeparator() {
        PacketTokenizer splitter = new PacketTokenizer("PK", "foo", ',');

        assertTrue(splitter.hasNext());
        assertEquals("foo", splitter.nextPart());

        assertFalse(splitter.hasNext());
        assertThrows(ParsePacketException.class, splitter::nextPart);

        try {
            splitter.nextPart();
            fail("ParsePacketException expected");
        } catch (ParsePacketException e) {
            assertEquals("PKfoo", e.packet());
        }
    }

    @Test
    void onlySeparators() {
        PacketTokenizer splitter = new PacketTokenizer("PK", ",,", ',');

        assertTrue(splitter.hasNext());
        assertEquals("", splitter.nextPart());

        assertTrue(splitter.hasNext());
        assertEquals("", splitter.nextPart());

        assertTrue(splitter.hasNext());
        assertEquals("", splitter.nextPart());

        assertFalse(splitter.hasNext());
        assertThrows(ParsePacketException.class, splitter::nextPart);

        try {
            splitter.nextPart();
            fail("ParsePacketException expected");
        } catch (ParsePacketException e) {
            assertEquals("PK,,", e.packet());
        }
    }
    @Test
    void nextPartOrDefault() {
        PacketTokenizer splitter = new PacketTokenizer("PK", "foo,bar", ',');

        assertEquals("foo", splitter.nextPartOrDefault("default"));
        assertEquals("bar", splitter.nextPartOrDefault("default"));
        assertEquals("default", splitter.nextPartOrDefault("default"));
        assertEquals("default", splitter.nextPartOrDefault("default"));

        try {
            splitter.nextPart();
            fail("ParsePacketException expected");
        } catch (ParsePacketException e) {
            assertEquals("PKfoo,bar", e.packet());
        }
    }

    @Test
    void nextInt() {
        PacketTokenizer splitter = new PacketTokenizer("PK", "12,-23,,invalid", ',');

        assertEquals(12, splitter.nextInt());
        assertEquals(-23, splitter.nextInt());
        assertThrows(ParsePacketException.class, splitter::nextInt);
        assertThrows(ParsePacketException.class, splitter::nextInt);
        assertThrows(ParsePacketException.class, splitter::nextInt);

        try {
            splitter.nextPart();
            fail("ParsePacketException expected");
        } catch (ParsePacketException e) {
            assertEquals("PK12,-23,,invalid", e.packet());
        }
    }

    @Test
    void nextIntOrDefault() {
        PacketTokenizer splitter = new PacketTokenizer("PK", "12,-23,,invalid", ',');

        assertEquals(12, splitter.nextIntOrDefault(42));
        assertEquals(-23, splitter.nextIntOrDefault(42));
        assertEquals(42, splitter.nextIntOrDefault(42));
        assertThrows(ParsePacketException.class, () -> splitter.nextIntOrDefault(42));
        assertEquals(42, splitter.nextIntOrDefault(42));

        try {
            splitter.nextPart();
            fail("ParsePacketException expected");
        } catch (ParsePacketException e) {
            assertEquals("PK12,-23,,invalid", e.packet());
        }
    }

    @Test
    void nextNonNegativeOrNegativeOneInt() {
        PacketTokenizer splitter = new PacketTokenizer("PK", "12,-23,-1,0,,invalid", ',');

        assertEquals(12, splitter.nextNonNegativeOrNegativeOneInt());
        assertThrows(ParsePacketException.class, splitter::nextNonNegativeOrNegativeOneInt);
        assertEquals(-1, splitter.nextNonNegativeOrNegativeOneInt());
        assertEquals(0, splitter.nextNonNegativeOrNegativeOneInt());
        assertThrows(ParsePacketException.class, splitter::nextNonNegativeOrNegativeOneInt);
        assertThrows(ParsePacketException.class, splitter::nextNonNegativeOrNegativeOneInt);
        assertThrows(ParsePacketException.class, splitter::nextNonNegativeOrNegativeOneInt);

        try {
            splitter.nextPart();
            fail("ParsePacketException expected");
        } catch (ParsePacketException e) {
            assertEquals("PK12,-23,-1,0,,invalid", e.packet());
        }
    }

    @Test
    void nextNonNegativeOrNegativeOneIntOrDefault() {
        PacketTokenizer splitter = new PacketTokenizer("PK", "12,-23,-1,0,,invalid", ',');

        assertEquals(12, splitter.nextNonNegativeOrNegativeOneIntOrDefault(42));
        assertThrows(ParsePacketException.class, () -> splitter.nextNonNegativeOrNegativeOneIntOrDefault(42));
        assertEquals(-1, splitter.nextNonNegativeOrNegativeOneIntOrDefault(42));
        assertEquals(0, splitter.nextNonNegativeOrNegativeOneIntOrDefault(42));
        assertEquals(42, splitter.nextNonNegativeOrNegativeOneIntOrDefault(42));
        assertThrows(ParsePacketException.class, () -> splitter.nextNonNegativeOrNegativeOneIntOrDefault(42));
        assertEquals(42, splitter.nextNonNegativeOrNegativeOneIntOrDefault(42));

        try {
            splitter.nextPart();
            fail("ParsePacketException expected");
        } catch (ParsePacketException e) {
            assertEquals("PK12,-23,-1,0,,invalid", e.packet());
        }
    }

    @Test
    void nextNonNegativeInt() {
        PacketTokenizer splitter = new PacketTokenizer("PK", "12,-23,-1,0,,invalid", ',');

        assertEquals(12, splitter.nextNonNegativeInt());
        assertThrows(ParsePacketException.class, splitter::nextNonNegativeInt);
        assertThrows(ParsePacketException.class, splitter::nextNonNegativeInt);
        assertEquals(0, splitter.nextNonNegativeInt());
        assertThrows(ParsePacketException.class, splitter::nextNonNegativeInt);
        assertThrows(ParsePacketException.class, splitter::nextNonNegativeInt);
        assertThrows(ParsePacketException.class, splitter::nextNonNegativeInt);

        try {
            splitter.nextPart();
            fail("ParsePacketException expected");
        } catch (ParsePacketException e) {
            assertEquals("PK12,-23,-1,0,,invalid", e.packet());
        }
    }

    @Test
    void nextNonNegativeIntOrDefault() {
        PacketTokenizer splitter = new PacketTokenizer("PK", "12,-23,-1,0,,invalid", ',');

        assertEquals(12, splitter.nextNonNegativeIntOrDefault(42));
        assertThrows(ParsePacketException.class, () -> splitter.nextNonNegativeIntOrDefault(42));
        assertThrows(ParsePacketException.class, () -> splitter.nextNonNegativeIntOrDefault(42));
        assertEquals(0, splitter.nextNonNegativeIntOrDefault(42));
        assertEquals(42, splitter.nextNonNegativeIntOrDefault(42));
        assertThrows(ParsePacketException.class, () -> splitter.nextNonNegativeIntOrDefault(42));
        assertEquals(42, splitter.nextNonNegativeIntOrDefault(42));

        try {
            splitter.nextPart();
            fail("ParsePacketException expected");
        } catch (ParsePacketException e) {
            assertEquals("PK12,-23,-1,0,,invalid", e.packet());
        }
    }

    @Test
    void nextPositiveInt() {
        PacketTokenizer splitter = new PacketTokenizer("PK", "12,-23,-1,0,1,,invalid", ',');

        assertEquals(12, splitter.nextPositiveInt());
        assertThrows(ParsePacketException.class, splitter::nextPositiveInt);
        assertThrows(ParsePacketException.class, splitter::nextPositiveInt);
        assertThrows(ParsePacketException.class, splitter::nextPositiveInt);
        assertEquals(1, splitter.nextPositiveInt());
        assertThrows(ParsePacketException.class, splitter::nextPositiveInt);
        assertThrows(ParsePacketException.class, splitter::nextPositiveInt);
        assertThrows(ParsePacketException.class, splitter::nextPositiveInt);

        try {
            splitter.nextPart();
            fail("ParsePacketException expected");
        } catch (ParsePacketException e) {
            assertEquals("PK12,-23,-1,0,1,,invalid", e.packet());
        }
    }

    @Test
    void nextPositiveIntOrDefault() {
        PacketTokenizer splitter = new PacketTokenizer("PK", "12,-23,-1,0,1,,invalid", ',');

        assertEquals(12, splitter.nextPositiveIntOrDefault(42));
        assertThrows(ParsePacketException.class, () -> splitter.nextPositiveIntOrDefault(42));
        assertThrows(ParsePacketException.class, () -> splitter.nextPositiveIntOrDefault(42));
        assertThrows(ParsePacketException.class, () -> splitter.nextPositiveIntOrDefault(42));
        assertEquals(1, splitter.nextPositiveIntOrDefault(42));
        assertEquals(42, splitter.nextPositiveIntOrDefault(42));
        assertThrows(ParsePacketException.class, () -> splitter.nextPositiveIntOrDefault(42));
        assertEquals(42, splitter.nextPositiveIntOrDefault(42));

        try {
            splitter.nextPart();
            fail("ParsePacketException expected");
        } catch (ParsePacketException e) {
            assertEquals("PK12,-23,-1,0,1,,invalid", e.packet());
        }
    }
}
