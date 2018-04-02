package fr.quatrevieux.araknemu.core.event;

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
    public <E extends Listener> E get(Class<E> listenerClass);

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
}
