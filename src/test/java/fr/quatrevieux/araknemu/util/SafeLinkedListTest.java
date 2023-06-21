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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SafeLinkedListTest {
    @Test
    void simpleAddAndIterate() {
        SafeLinkedList<Integer> list = new SafeLinkedList<>();
        assertIterableEquals(Collections.emptyList(), list);

        list.add(5);
        assertIterableEquals(Collections.singletonList(5), list);

        list.add(6);
        assertIterableEquals(Arrays.asList(5, 6), list);

        list.add(8);
        list.add(15);
        assertIterableEquals(Arrays.asList(5, 6, 8, 15), list);
    }

    @Test
    void addAndRemove() {
        SafeLinkedList<Integer> list = new SafeLinkedList<>();

        list.add(1);
        list.add(2);
        list.add(3);

        Iterator<Integer> it = list.iterator();
        while (it.hasNext()) {
            if (it.next() == 2) {
                it.remove();
            }
        }

        assertIterableEquals(Arrays.asList(1, 3), list);

        it = list.iterator();
        while (it.hasNext()) {
            if (it.next() == 1) {
                it.remove();
            }
        }

        assertIterableEquals(Collections.singletonList(3), list);

        it = list.iterator();
        while (it.hasNext()) {
            if (it.next() == 3) {
                it.remove();
            }
        }

        assertIterableEquals(Collections.emptyList(), list);
    }

    @Test
    void removeMultiple() {
        SafeLinkedList<Integer> list = new SafeLinkedList<>();

        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);

        Iterator<Integer> it = list.iterator();
        while (it.hasNext()) {
            final int i = it.next();

            if (i > 2 && i < 6) {
                it.remove();
            }
        }

        assertIterableEquals(Arrays.asList(1, 2, 6), list);

        it = list.iterator();
        while (it.hasNext()) {
            final int i = it.next();

            if (i < 6) {
                it.remove();
            }
        }

        assertIterableEquals(Collections.singletonList(6), list);
    }

    @Test
    void removeTwiceNotAllowed() {
        SafeLinkedList<Integer> list = new SafeLinkedList<>();

        list.add(1);
        list.add(2);
        list.add(3);

        Iterator<Integer> it = list.iterator();
        it.next();
        it.remove();
        assertThrows(IllegalStateException.class, it::remove);

        assertIterableEquals(Arrays.asList(2, 3), list);
    }

    @Test
    void removeNextNotCalled() {
        SafeLinkedList<Integer> list = new SafeLinkedList<>();

        list.add(1);
        list.add(2);
        list.add(3);

        Iterator<Integer> it = list.iterator();
        assertThrows(IllegalStateException.class, it::remove);

        assertIterableEquals(Arrays.asList(1, 2, 3), list);
    }

    @Test
    void nextOnEmptyList() {
        SafeLinkedList<Integer> list = new SafeLinkedList<>();

        Iterator<Integer> it = list.iterator();
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void nextAfterEndOfList() {
        SafeLinkedList<Integer> list = new SafeLinkedList<>();

        list.add(1);
        list.add(2);
        list.add(3);

        Iterator<Integer> it = list.iterator();
        it.next();
        it.next();
        it.next();

        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void addDuringIteration() {
        SafeLinkedList<Integer> list = new SafeLinkedList<>();

        list.add(1);
        list.add(2);
        list.add(3);

        List<Integer> walked = new ArrayList<>();

        for (int i : list) {
            walked.add(i);

            if (i < 10) {
                list.add(i + 10);
            }
        }

        assertIterableEquals(Arrays.asList(1, 2, 3, 11, 12, 13), walked);
        assertIterableEquals(Arrays.asList(1, 2, 3, 11, 12, 13), list);
    }

    @Test
    void addDuringIterationOfIteration() {
        SafeLinkedList<Integer> list = new SafeLinkedList<>();

        list.add(1);
        list.add(2);
        list.add(3);

        List<Integer> walked = new ArrayList<>();

        for (int i : list) {
            walked.add(i);

            for (int j : list) {
                if (i + j < 10) {
                    list.add(10 + i + j);
                }
            }
        }

        assertIterableEquals(Arrays.asList(1, 2, 3, 12, 13, 14, 13, 14, 15, 14, 15, 16), walked);
        assertIterableEquals(Arrays.asList(1, 2, 3, 12, 13, 14, 13, 14, 15, 14, 15, 16), list);
    }
}
