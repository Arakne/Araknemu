package fr.quatrevieux.araknemu.game.handler.loader;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Aggregate loaders
 */
final public class AggregateLoader implements Loader {
    final private Loader[] loaders;

    public AggregateLoader(Loader... loaders) {
        this.loaders = loaders;
    }

    @Override
    public PacketHandler<GameSession, ?>[] load(Container container) throws ContainerException {
        Collection<PacketHandler<GameSession, ?>> handlers = new ArrayList<>();

        for (Loader loader : loaders) {
            handlers.addAll(
                Arrays.asList(loader.load(container))
            );
        }

        return handlers.toArray(new PacketHandler[0]);
    }
}
