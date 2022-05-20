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

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Simple implementation of linked list with safe add and remove operations
 *
 * - Adding an element while iterating will be added to the end, and taken in account by the iterator (i.e. will iterator on it)
 * - Removing an element can only be performed during iteration, and will not break other iterators
 *
 * This code is allowed :
 * <pre>{@code
 * SafeLinkedList<MyObject> list = new SafeLinkedList<>();
 * // Add some elements...
 *
 * Iterator<MyObject> it = list.iterator();
 *
 * while (it.hasNext()) {
 *     MyObject o = it.next();
 *
 *     if (o.shouldBeDeleted()) {
 *         it.remove();
 *     }
 *
 *     if (o.hasChild()) {
 *         list.add(o.getChild());
 *     }
 * }
 * }</pre>
 *
 * @param <E> Stored element type
 */
public final class SafeLinkedList<E> implements Iterable<E> {
    private Node<E> first;
    private Node<E> last;

    /**
     * Add an element to the end of the list
     *
     * Unlike default {@link java.util.LinkedList}, calling this method will not raise an
     * {@link java.util.ConcurrentModificationException} while iterating.
     *
     * @param element Element to add
     */
    public void add(E element) {
        final Node<E> newNode = new Node<>(element);

        if (last != null) {
            last.next = newNode;
            last = newNode;
            return;
        }

        first = last = newNode;
    }

    @Override
    public Iterator<E> iterator() {
        return new ListIter();
    }

    private static class Node<E> {
        private final E element;
        private Node<E> next;

        public Node(E element) {
            this.element = element;
        }
    }

    private class ListIter implements Iterator<E> {
        private SafeLinkedList.@Nullable Node<E> current = null;
        private SafeLinkedList.@Nullable Node<E> previous = null;

        @Override
        public boolean hasNext() {
            // First loop
            if (current == null) {
                return first != null;
            }

            return current.next != null;
        }

        @Override
        public E next() {
            if (current == null) {
                current = first;
            } else {
                previous = current;
                current = current.next;
            }

            if (current == null) {
                throw new NoSuchElementException();
            }

            return current.element;
        }

        @Override
        public void remove() {
            final Node<E> current = this.current;

            // next not yet called, or remove already called (current set to previous)
            if (current == null || previous == current) {
                throw new IllegalStateException();
            }

            // Remove the first element
            if (current == first) {
                first = current.next;
            }

            // Remove the last element
            if (current == last) {
                last = previous;
            }

            // Detach current node
            if (previous != null) {
                previous.next = current.next;
            }

            // Change "current" node
            this.current = previous;
        }
    }
}
