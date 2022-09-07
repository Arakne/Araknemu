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
import org.checkerframework.checker.nullness.qual.EnsuresKeyForIf;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Default implementation for dispatcher, using maps
 *
 * Adding or removing listeners is not thread safe
 */
public final class DefaultListenerAggregate implements ListenerAggregate {
    private static final Logger defaultLogger = LogManager.getLogger(DefaultListenerAggregate.class);

    private final Logger logger;

    /**
     * Store listeners instances by their class
     * Ensure that only one listener of a given type is registered
     */
    private final Map<Class<? extends Listener>, Listener> listeners = new HashMap<>();

    /**
     * Store listeners containers by their handled event class
     */
    private final Map<Class, ListenersContainer<?>> containers = new HashMap<>();

    public DefaultListenerAggregate() {
        this(defaultLogger);
    }

    public DefaultListenerAggregate(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void dispatch(Object event) {
        final ListenersContainer<?> container = containers.get(event.getClass());

        if (container == null || container.isEmpty()) {
            return;
        }

        final boolean lastUse = container.use();

        try {
            // @todo use generics on dipatch parameter
            for (Listener listener : container) {
                try {
                    listener.on(event);
                } catch (RuntimeException e) {
                    logger.error("Error during execution of listener " + listener.getClass().getName(), e);
                }
            }
        } finally {
            if (!lastUse) {
                container.release();
            }
        }
    }

    @Override
    public void add(Listener listener) {
        final Class<? extends Listener> listenerClass = listener.getClass();

        final Listener lastListener = listeners.put(listenerClass, listener);

        if (lastListener != null) {
            remove(lastListener);
        }

        final Class eventClass = listener.event();
        ListenersContainer<?> eventListeners = containers.get(eventClass);

        if (eventListeners == null) {
            eventListeners = new ListenersContainer<>();
            containers.put(eventClass, eventListeners);
        } else if (eventListeners.inUse()) {
            // Current event is in use : duplicate container for ignore concurrent modification
            // See: https://github.com/Arakne/Araknemu/issues/250
            eventListeners = new ListenersContainer<>(eventListeners);
            containers.put(eventClass, eventListeners);
        }

        eventListeners.add(listener);
    }

    @Override
    @EnsuresKeyForIf(expression = "#1", map = "listeners", result = true)
    public boolean has(Class<? extends Listener> listenerClass) {
        return listeners.containsKey(listenerClass);
    }

    @Override
    public void remove(Class<? extends Listener> listenerClass) {
        final Listener<?> listener = listeners.remove(listenerClass);

        if (listener != null) {
            remove(listener);
        }
    }

    @Override
    @SuppressWarnings({"unchecked", "cast.unsafe"})
    public <E extends Listener> @Nullable E get(Class<E> listenerClass) {
        return (E) listeners.get(listenerClass);
    }

    /**
     * Remove a listener instance from container
     */
    private <E> void remove(Listener<E> listener) {
        final Class<E> eventClass = listener.event();
        ListenersContainer<E> container = (ListenersContainer<E>) containers.get(eventClass);

        if (container == null) {
            return;
        }

        // Only requested listener is registered : simply remove the container
        if (container.isSingleton()) {
            containers.remove(eventClass);
            return;
        }

        // Requested listener is in use : duplicate container for ignore concurrent modification
        // See: https://github.com/Arakne/Araknemu/issues/250
        if (container.inUse()) {
            container = new ListenersContainer<>(container);
            containers.put(eventClass, container);
        }

        container.remove(listener);
    }

    /**
     * Contains listeners instances and "inUse" flag for handle concurrent modification
     *
     * See: https://github.com/Arakne/Araknemu/issues/250
     *
     * @param <E> Handled event class
     */
    private static class ListenersContainer<E> implements Iterable<Listener<E>> {
        private final Collection<Listener<E>> listeners;
        private final AtomicBoolean inUse = new AtomicBoolean(false);

        public ListenersContainer(ListenersContainer<E> other) {
            this.listeners = new ArrayList<>(other.listeners);
        }

        public ListenersContainer() {
            this.listeners = new ArrayList<>();
        }

        /**
         * Add a new listener to the container
         * Note: container must not be in use
         */
        public void add(Listener<E> listener) {
            assert !inUse();
            listeners.add(listener);
        }

        /**
         * Remove listener from the container
         * Note: container must not be in use
         */
        public void remove(Listener<E> listener) {
            assert !inUse();
            listeners.remove(listener);
        }

        /**
         * Does the container is empty ? (i.e. do not have any listeners)
         */
        public boolean isEmpty() {
            return listeners.isEmpty();
        }

        /**
         * Does the container has only one listener ?
         */
        public boolean isSingleton() {
            return listeners.size() == 1;
        }

        /**
         * Set the flag "inUse" to true
         *
         * @return The last state of "inUse" flag
         */
        public boolean use() {
            return inUse.getAndSet(true);
        }

        /**
         * Check if the "inUse" flag value
         * If this value is true, modification of listeners is disallowed
         */
        public boolean inUse() {
            return inUse.get();
        }

        /**
         * Set the flag "inUse" to false
         */
        public void release() {
            inUse.set(false);
        }

        @Override
        public Iterator<Listener<E>> iterator() {
            return listeners.iterator();
        }
    }
}
