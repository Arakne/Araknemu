package fr.quatrevieux.araknemu.game;

import fr.quatrevieux.araknemu.core.BootException;
import fr.quatrevieux.araknemu.core.Service;
import fr.quatrevieux.araknemu.game.connector.RealmConnector;
import fr.quatrevieux.araknemu.network.Server;
import fr.quatrevieux.araknemu.realm.host.GameHost;
import org.apache.mina.core.service.IoHandler;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Collection;

/**
 * Service for game server
 */
final public class GameService implements Service {
    final private GameConfiguration configuration;
    final private RealmConnector connector;
    final private IoHandler ioHandler;
    final private Collection<PreloadableService> preloadables;
    private final Logger logger;

    private Server server;

    public GameService(GameConfiguration configuration, RealmConnector connector, IoHandler ioHandler, Logger logger, Collection<PreloadableService> preloadables) {
        this.configuration = configuration;
        this.connector = connector;
        this.ioHandler = ioHandler;
        this.preloadables = preloadables;
        this.logger = logger;
    }

    @Override
    public void boot() throws BootException {
        for (PreloadableService service : preloadables) {
            service.preload(logger);
        }

        try {
            server = new Server(
                ioHandler,
                configuration.port()
            );
        } catch (IOException e) {
            throw new BootException(e);
        }

        connector.declare(
            configuration.id(),
            configuration.port(),
            configuration.ip()
        );

        connector.updateState(
            configuration.id(),
            GameHost.State.ONLINE,
            true
        );
    }

    @Override
    public void shutdown() {
        connector.updateState(
            configuration.id(),
            GameHost.State.OFFLINE,
            false
        );

        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
