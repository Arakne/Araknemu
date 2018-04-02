package fr.quatrevieux.araknemu.core.event;

import java.util.Collection;

/**
 * Subscribe to events registering listeners
 */
public interface EventsSubscriber {
    /**
     * Get listeners to subscribe
     */
    public Listener[] listeners();
}
