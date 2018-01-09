package fr.quatrevieux.araknemu.game.handler.loader;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Base loader class
 */
abstract public class AbstractLoader implements Loader {
    final private Function<PacketHandler, PacketHandler> wrapper;

    public AbstractLoader(Function<PacketHandler, PacketHandler> wrapper) {
        this.wrapper = wrapper;
    }

    abstract protected PacketHandler<GameSession, ?>[] handlers(Container container) throws ContainerException;

    @Override
    public PacketHandler<GameSession, ?>[] load(Container container) throws ContainerException {
        return Arrays.stream(handlers(container))
            .map(wrapper)
            .toArray(PacketHandler[]::new)
        ;
    }
}
