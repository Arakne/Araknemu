package fr.quatrevieux.araknemu.game.event;

/**
 * Listener for events
 *
 * @param <E> The event to listen
 */
public interface Listener<E> {
    /**
     * Handle the event
     *
     * @param event The event instance
     */
    public void on(E event);

    /**
     * Get the event class
     */
    public Class<E> event();
}
