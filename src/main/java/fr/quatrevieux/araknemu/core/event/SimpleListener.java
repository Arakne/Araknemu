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

package fr.quatrevieux.araknemu.core.event;

import java.util.function.Consumer;

/**
 * Simple implementation of listener using {@link Consumer}
 *
 * dispatcher.add(
 *     new SimpleListener<>(MyEvent.class, (evt) -> doSomething(evt))
 * );
 */
public final class SimpleListener<E> implements Listener<E> {
    private final Class<E> type;
    private final Consumer<E> consumer;

    public SimpleListener(Class<E> type, Consumer<E> consumer) {
        this.type = type;
        this.consumer = consumer;
    }

    @Override
    public void on(E event) {
        consumer.accept(event);
    }

    @Override
    public Class<E> event() {
        return type;
    }
}
