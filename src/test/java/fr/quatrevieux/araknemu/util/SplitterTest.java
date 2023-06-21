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

package fr.quatrevieux.araknemu.util;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SplitterTest {
    @Test
    void empty() {
        Splitter splitter = new Splitter("", ',');

        assertEquals("", splitter.value());
        assertTrue(splitter.hasNext());
        assertEquals("", splitter.nextPart());

        assertFalse(splitter.hasNext());
        assertThrows(NoSuchElementException.class, splitter::nextPart);
    }

    @Test
    void withoutSeparator() {
        Splitter splitter = new Splitter("foo", ',');

        assertTrue(splitter.hasNext());
        assertEquals("foo", splitter.nextPart());

        assertFalse(splitter.hasNext());
        assertThrows(NoSuchElementException.class, splitter::nextPart);
    }

    @Test
    void onlySeparators() {
        Splitter splitter = new Splitter(",,", ',');

        assertTrue(splitter.hasNext());
        assertEquals("", splitter.nextPart());

        assertTrue(splitter.hasNext());
        assertEquals("", splitter.nextPart());

        assertTrue(splitter.hasNext());
        assertEquals("", splitter.nextPart());

        assertFalse(splitter.hasNext());
        assertThrows(NoSuchElementException.class, splitter::nextPart);
    }
    @Test
    void nextPartOrDefault() {
        Splitter splitter = new Splitter("foo,bar", ',');

        assertEquals("foo", splitter.nextPartOrDefault("default"));
        assertEquals("bar", splitter.nextPartOrDefault("default"));
        assertEquals("default", splitter.nextPartOrDefault("default"));
        assertEquals("default", splitter.nextPartOrDefault("default"));
    }

    @Test
    void nextInt() {
        Splitter splitter = new Splitter("12,-23,,invalid", ',');

        assertEquals(12, splitter.nextInt());
        assertEquals(-23, splitter.nextInt());
        assertThrows(NumberFormatException.class, splitter::nextInt);
        assertThrows(NumberFormatException.class, splitter::nextInt);
        assertThrows(NoSuchElementException.class, splitter::nextInt);
    }

    @Test
    void nextIntOrDefault() {
        Splitter splitter = new Splitter("12,-23,,invalid", ',');

        assertEquals(12, splitter.nextIntOrDefault(42));
        assertEquals(-23, splitter.nextIntOrDefault(42));
        assertEquals(42, splitter.nextIntOrDefault(42));
        assertThrows(NumberFormatException.class, () -> splitter.nextIntOrDefault(42));
        assertEquals(42, splitter.nextIntOrDefault(42));
    }

    @Test
    void nextIntWithBase() {
        Splitter splitter = new Splitter("fa,-a,,invalid", ',');

        assertEquals(250, splitter.nextInt(16));
        assertEquals(-10, splitter.nextInt(16));
        assertThrows(NumberFormatException.class, () -> splitter.nextInt(16));
        assertThrows(NumberFormatException.class, () -> splitter.nextInt(16));
        assertThrows(NoSuchElementException.class, () -> splitter.nextInt(16));
    }

    @Test
    void nextNonNegativeOrNegativeOneInt() {
        Splitter splitter = new Splitter("12,-23,-1,0,,invalid", ',');

        assertEquals(12, splitter.nextNonNegativeOrNegativeOneInt());
        assertThrows(IllegalArgumentException.class, splitter::nextNonNegativeOrNegativeOneInt);
        assertEquals(-1, splitter.nextNonNegativeOrNegativeOneInt());
        assertEquals(0, splitter.nextNonNegativeOrNegativeOneInt());
        assertThrows(NumberFormatException.class, splitter::nextNonNegativeOrNegativeOneInt);
        assertThrows(NumberFormatException.class, splitter::nextNonNegativeOrNegativeOneInt);
        assertThrows(NoSuchElementException.class, splitter::nextNonNegativeOrNegativeOneInt);
    }

    @Test
    void nextNonNegativeOrNegativeOneIntOrDefault() {
        Splitter splitter = new Splitter("12,-23,-1,0,,invalid", ',');

        assertEquals(12, splitter.nextNonNegativeOrNegativeOneIntOrDefault(42));
        assertThrows(IllegalArgumentException.class, () -> splitter.nextNonNegativeOrNegativeOneIntOrDefault(42));
        assertEquals(-1, splitter.nextNonNegativeOrNegativeOneIntOrDefault(42));
        assertEquals(0, splitter.nextNonNegativeOrNegativeOneIntOrDefault(42));
        assertEquals(42, splitter.nextNonNegativeOrNegativeOneIntOrDefault(42));
        assertThrows(NumberFormatException.class, () -> splitter.nextNonNegativeOrNegativeOneIntOrDefault(42));
        assertEquals(42, splitter.nextNonNegativeOrNegativeOneIntOrDefault(42));
    }

    @Test
    void nextNonNegativeInt() {
        Splitter splitter = new Splitter("12,-23,-1,0,,invalid", ',');

        assertEquals(12, splitter.nextNonNegativeInt());
        assertThrows(IllegalArgumentException.class, splitter::nextNonNegativeInt);
        assertThrows(IllegalArgumentException.class, splitter::nextNonNegativeInt);
        assertEquals(0, splitter.nextNonNegativeInt());
        assertThrows(NumberFormatException.class, splitter::nextNonNegativeInt);
        assertThrows(NumberFormatException.class, splitter::nextNonNegativeInt);
        assertThrows(NoSuchElementException.class, splitter::nextNonNegativeInt);
    }

    @Test
    void nextNonNegativeIntOrDefault() {
        Splitter splitter = new Splitter("12,-23,-1,0,,invalid", ',');

        assertEquals(12, splitter.nextNonNegativeIntOrDefault(42));
        assertThrows(IllegalArgumentException.class, () -> splitter.nextNonNegativeIntOrDefault(42));
        assertThrows(IllegalArgumentException.class, () -> splitter.nextNonNegativeIntOrDefault(42));
        assertEquals(0, splitter.nextNonNegativeIntOrDefault(42));
        assertEquals(42, splitter.nextNonNegativeIntOrDefault(42));
        assertThrows(NumberFormatException.class, () -> splitter.nextNonNegativeIntOrDefault(42));
        assertEquals(42, splitter.nextNonNegativeIntOrDefault(42));
    }

    @Test
    void nextNonNegativeIntWithBase() {
        Splitter splitter = new Splitter("c,-f,-1,0,fa,,invalid", ',');

        assertEquals(12, splitter.nextNonNegativeInt(16));
        assertThrows(IllegalArgumentException.class, () -> splitter.nextNonNegativeInt(16));
        assertThrows(IllegalArgumentException.class, () -> splitter.nextNonNegativeInt(16));
        assertEquals(0, splitter.nextNonNegativeInt(16));
        assertEquals(250, splitter.nextNonNegativeInt(16));
        assertThrows(NumberFormatException.class, () -> splitter.nextNonNegativeInt(16));
        assertThrows(NumberFormatException.class, () -> splitter.nextNonNegativeInt(16));
        assertThrows(NoSuchElementException.class, () -> splitter.nextNonNegativeInt(16));
    }

    @Test
    void nextPositiveInt() {
        Splitter splitter = new Splitter("12,-23,-1,0,1,,invalid", ',');

        assertEquals(12, splitter.nextPositiveInt());
        assertThrows(IllegalArgumentException.class, splitter::nextPositiveInt);
        assertThrows(IllegalArgumentException.class, splitter::nextPositiveInt);
        assertThrows(IllegalArgumentException.class, splitter::nextPositiveInt);
        assertEquals(1, splitter.nextPositiveInt());
        assertThrows(NumberFormatException.class, splitter::nextPositiveInt);
        assertThrows(NumberFormatException.class, splitter::nextPositiveInt);
        assertThrows(NoSuchElementException.class, splitter::nextPositiveInt);
    }

    @Test
    void nextPositiveIntOrDefault() {
        Splitter splitter = new Splitter("12,-23,-1,0,1,,invalid", ',');

        assertEquals(12, splitter.nextPositiveIntOrDefault(42));
        assertThrows(IllegalArgumentException.class, () -> splitter.nextPositiveIntOrDefault(42));
        assertThrows(IllegalArgumentException.class, () -> splitter.nextPositiveIntOrDefault(42));
        assertThrows(IllegalArgumentException.class, () -> splitter.nextPositiveIntOrDefault(42));
        assertEquals(1, splitter.nextPositiveIntOrDefault(42));
        assertEquals(42, splitter.nextPositiveIntOrDefault(42));
        assertThrows(NumberFormatException.class, () -> splitter.nextPositiveIntOrDefault(42));
        assertEquals(42, splitter.nextPositiveIntOrDefault(42));
    }

    @Test
    void nextSplit() {
        Splitter splitter = new Splitter("a,b;c,d;e", ';');

        Splitter subSplit = splitter.nextSplit(',');
        assertEquals("a,b", subSplit.value());
        assertEquals("a", subSplit.nextPart());
        assertEquals("b", subSplit.nextPart());
        assertThrows(NoSuchElementException.class, subSplit::nextPart);

        subSplit = splitter.nextSplit(',');
        assertEquals("c,d", subSplit.value());
        assertEquals("c", subSplit.nextPart());
        assertEquals("d", subSplit.nextPart());
        assertThrows(NoSuchElementException.class, subSplit::nextPart);

        subSplit = splitter.nextSplit(',');
        assertEquals("e", subSplit.value());
        assertEquals("e", subSplit.nextPart());
        assertThrows(NoSuchElementException.class, subSplit::nextPart);
    }

    @Test
    void toIntArray() {
        assertArrayEquals(new int[] {123, 45, 74}, new Splitter("123;45;74", ';').toIntArray());
        assertArrayEquals(new int[] {123}, new Splitter("123", ';').toIntArray());
        assertArrayEquals(new int[] {}, new Splitter("", ';').toIntArray());
        assertThrows(NumberFormatException.class, new Splitter("invalid", ';')::toIntArray);
    }

    @Test
    void toIntArrayShouldMoveCursorToEnd() {
        Splitter splitter = new Splitter("12;34", ';');

        assertArrayEquals(new int[] {12, 34}, splitter.toIntArray());
        assertFalse(splitter.hasNext());
        assertThrows(NoSuchElementException.class, splitter::nextPart);
        assertThrows(NoSuchElementException.class, splitter::toIntArray);

        splitter = new Splitter("", ';');
        assertArrayEquals(new int[0], splitter.toIntArray());
        assertFalse(splitter.hasNext());
        assertThrows(NoSuchElementException.class, splitter::nextPart);
        assertThrows(NoSuchElementException.class, splitter::toIntArray);

        splitter = new Splitter("foo,bar,147,52", ',');

        assertEquals("foo", splitter.nextPart());
        assertEquals("bar", splitter.nextPart());
        assertArrayEquals(new int[] {147, 52}, splitter.toIntArray());
        assertFalse(splitter.hasNext());

        splitter = new Splitter("foo,", ',');

        assertEquals("foo", splitter.nextPart());
        assertArrayEquals(new int[0], splitter.toIntArray());
        assertFalse(splitter.hasNext());
    }
}
