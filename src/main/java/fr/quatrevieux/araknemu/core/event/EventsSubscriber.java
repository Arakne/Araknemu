package fr.quatrevieux.araknemu.core.event;

/**
 * Subscribe to events registering listeners
 */
public interface EventsSubscriber {
    /**
     * Get listeners to subscribe
     */
    public Listener[] listeners();
}
