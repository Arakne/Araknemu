package fr.quatrevieux.araknemu.game.handler.loader;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Loader for packets handlers
 */
public interface Loader {
    /**
     * Load handler
     *
     * @param container The current DI container
     */
    public PacketHandler<GameSession, ?>[] load(Container container) throws ContainerException;
}
