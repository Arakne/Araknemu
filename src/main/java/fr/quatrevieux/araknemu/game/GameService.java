package fr.quatrevieux.araknemu.game;

import fr.quatrevieux.araknemu.core.BootException;
import fr.quatrevieux.araknemu.core.Service;
import fr.quatrevieux.araknemu.game.connector.RealmConnector;
import fr.quatrevieux.araknemu.network.Server;
import fr.quatrevieux.araknemu.realm.host.GameHost;
import org.apache.mina.core.service.IoHandler;

import java.io.IOException;

/**
 * Service for game server
 */
final public class GameService implements Service {
    final private GameConfiguration configuration;
    final private RealmConnector connector;
    final private IoHandler ioHandler;

    private Server server;

    public GameService(GameConfiguration configuration, RealmConnector connector, IoHandler ioHandler) {
        this.configuration = configuration;
        this.connector = connector;
        this.ioHandler = ioHandler;
    }

    @Override
    public void boot() throws BootException {
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
