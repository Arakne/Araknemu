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

package fr.quatrevieux.araknemu.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CopyOnFirstWriteCollectionTest {
    @Test
    void empty() {
        CopyOnFirstWriteCollection collection = new CopyOnFirstWriteCollection(new ArrayList());

        assertTrue(collection.isEmpty());
        assertEquals(0, collection.size());
        assertEquals(0, collection.toArray().length);
        assertEquals(0, collection.toArray(new Object[0]).length);

        collection.clear();
        assertTrue(collection.isEmpty());
    }

    @Test
    void read() {
        CopyOnFirstWriteCollection<Integer> collection = new CopyOnFirstWriteCollection<>(Arrays.asList(1, 2, 3));

        assertEquals(3, collection.size());
        assertFalse(collection.isEmpty());
        assertTrue(collection.contains(1));
        assertFalse(collection.contains(5));
        assertTrue(collection.containsAll(Arrays.asList(1, 3)));
        assertArrayEquals(new Object[] {1, 2, 3}, collection.toArray());
        assertArrayEquals(new Integer[] {1, 2, 3}, collection.toArray(new Integer[0]));
        assertArrayEquals(new Object[] {1, 2, 3}, collection.stream().toArray());
    }

    @Test
    void write() {
        CopyOnFirstWriteCollection<Integer> collection = new CopyOnFirstWriteCollection<>(Arrays.asList(1, 2, 3));

        collection.add(4);

        assertTrue(collection.contains(4));
        assertEquals(4, collection.size());

        collection.remove(2);
        assertFalse(collection.contains(2));
        assertEquals(3, collection.size());

        collection.addAll(Arrays.asList(8, 9));
        assertTrue(collection.contains(8));
        assertTrue(collection.contains(9));
        assertEquals(5, collection.size());

        collection.removeAll(Arrays.asList(3, 4));
        assertFalse(collection.contains(3));
        assertFalse(collection.contains(4));
        assertEquals(3, collection.size());

        collection.retainAll(Arrays.asList(1, 7, 8));
        assertEquals(2, collection.size());
        assertTrue(collection.contains(1));
        assertTrue(collection.contains(8));
        assertArrayEquals(new Object[] {1, 8}, collection.stream().toArray());
    }

    @Test
    void clearNotEmptyAndNotCopied() {
        CopyOnFirstWriteCollection<Integer> collection = new CopyOnFirstWriteCollection<>(Arrays.asList(1, 2, 3));

        collection.clear();

        assertTrue(collection.isEmpty());
    }

    @Test
    void clearNotEmptyCopied() {
        CopyOnFirstWriteCollection<Integer> collection = new CopyOnFirstWriteCollection<>(Arrays.asList(1, 2, 3));
        collection.add(4);

        collection.clear();

        assertTrue(collection.isEmpty());
    }

    @Test
    void customCollectionFactory() {
        CopyOnFirstWriteCollection<Integer> collection = new CopyOnFirstWriteCollection<>(
            Arrays.asList(1, 2, 3),
            HashSet::new
        );

        assertFalse(collection.add(3));
        assertEquals(3, collection.size());

        assertTrue(collection.add(4));
        assertEquals(4, collection.size());
    }
}
