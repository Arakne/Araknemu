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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Default implementation for dispatcher, using maps
 *
 * @fixme Use ConcurrentHashMap instead of HashMap ?
 */
public final class DefaultListenerAggregate implements ListenerAggregate {
    private static final Logger defaultLogger = LogManager.getLogger(DefaultListenerAggregate.class);

    private final Logger logger;
    private final Map<Class<? extends Listener>, Listener> listeners = new HashMap<>();
    private final Map<Class, Set<Class<? extends Listener>>> events = new HashMap<>();

    public DefaultListenerAggregate() {
        this(defaultLogger);
    }

    public DefaultListenerAggregate(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void dispatch(Object event) {
        if (!events.containsKey(event.getClass())) {
            return;
        }

        for (Class listenerClass : events.get(event.getClass())) {
            try {
                get(listenerClass).on(event);
            } catch (RuntimeException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void add(Listener listener) {
        if (!events.containsKey(listener.event())) {
            events.put(listener.event(), new LinkedHashSet<>());
        }

        listeners.put(listener.getClass(), listener);
        events.get(listener.event()).add(listener.getClass());
    }

    @Override
    public boolean has(Class<? extends Listener> listenerClass) {
        return listeners.containsKey(listenerClass);
    }

    @Override
    public void remove(Class<? extends Listener> listenerClass) {
        if (!has(listenerClass)) {
            return;
        }

        final Listener listener = listeners.remove(listenerClass);

        events.get(listener.event()).remove(listenerClass);
    }

    @Override
    public <E extends Listener> E get(Class<E> listenerClass) {
        return (E) listeners.get(listenerClass);
    }
}
