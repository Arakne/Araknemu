package fr.quatrevieux.araknemu.game.event;

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
}
