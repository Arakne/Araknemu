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

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.function.Consumer;

/**
 * Handle listener and dispatch events
 */
public interface ListenerAggregate extends Dispatcher {

    /**
     * Register the listener to the dispatcher
     *
     * @param listener Listener to register
     */
    public void add(Listener listener);

    /**
     * Check if the dispatcher has registered the listener class
     *
     * @param listenerClass Listener class to check
     */
    public boolean has(Class<? extends Listener> listenerClass);

    /**
     * Remove the listener
     *
     * @param listenerClass The listener class to remove
     */
    public void remove(Class<? extends Listener> listenerClass);

    /**
     * Get a listener from its class
     *
     * @param listenerClass The listener class
     */
    public <E extends Listener> @Nullable E get(Class<E> listenerClass);

    /**
     * Register a listener using {@link SimpleListener}
     *
     * @param eventClass The event to listen
     * @param consumer   The action to perform
     *
     * @param <E> The event type
     */
    public default <E> void add(Class<E> eventClass, Consumer<E> consumer) {
        add(
            new SimpleListener<>(eventClass, consumer)
        );
    }

    /**
     * Register an event subscriber
     *
     * @param subscriber The event subscriber
     */
    public default void register(EventsSubscriber subscriber) {
        for (Listener listener : subscriber.listeners()) {
            add(listener);
        }
    }

    /**
     * Remove all events of the subscriber
     *
     * @param subscriber The event subscriber
     */
    public default void unregister(EventsSubscriber subscriber) {
        for (Listener listener : subscriber.listeners()) {
            remove(listener.getClass());
        }
    }
}
