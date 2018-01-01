package fr.quatrevieux.araknemu.game;

import org.slf4j.Logger;

/**
 * Interface for service which can be preload on boot
 */
public interface PreloadableService {
    /**
     * Preload the service
     */
    public void preload(Logger logger);
}
