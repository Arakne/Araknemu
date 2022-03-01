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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Function;

/**
 * Wrap a collection and make a copy on write operation
 * to ensure that the original collection is never modified
 *
 * To keep the same collection type, you should define a custom copy factory (by default use {@link ArrayList})
 *
 * @param <E> The collection element type
 */
public final class CopyOnFirstWriteCollection<E> implements Collection<E> {
    private Collection<E> inner;
    private @Nullable Function<Collection<E>, Collection<E>> copyFactory;

    public CopyOnFirstWriteCollection(Collection<E> inner, Function<Collection<E>, Collection<E>> copyFactory) {
        this.inner = inner;
        this.copyFactory = copyFactory;
    }

    public CopyOnFirstWriteCollection(Collection<E> inner) {
        this(inner, ArrayList::new);
    }

    @Override
    public int size() {
        return inner.size();
    }

    @Override
    public boolean isEmpty() {
        return inner.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return inner.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        if (copyFactory == null) {
            return inner.iterator();
        }

        // Overrides iterator to disallow remove()
        return new Iterator<E>() {
            private final Iterator<E> inner = CopyOnFirstWriteCollection.this.inner.iterator();

            @Override
            public boolean hasNext() {
                return inner.hasNext();
            }

            @Override
            public E next() {
                return inner.next();
            }
        };
    }

    @Override
    @SuppressWarnings("return")
    public Object[] toArray() {
        return inner.toArray();
    }

    @Override
    @SuppressWarnings({"override.param", "return"})
    public <T> T @NonNull[] toArray(T @NonNull[] a) {
        return inner.toArray(a);
    }

    @Override
    public boolean add(E e) {
        return copy().add(e);
    }

    @Override
    public boolean remove(Object o) {
        return copy().remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return inner.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return copy().addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return copy().removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return copy().retainAll(c);
    }

    @Override
    public void clear() {
        if (inner.isEmpty()) {
            return;
        }

        if (copyFactory != null) {
            inner = copyFactory.apply(Collections.emptyList());
            copyFactory = null;
        } else {
            inner.clear();
        }
    }

    /**
     * Get the copy of the inner collection, if not already done
     */
    private Collection<E> copy() {
        if (copyFactory == null) {
            return inner;
        }

        inner = copyFactory.apply(inner);
        copyFactory = null;

        return inner;
    }
}
