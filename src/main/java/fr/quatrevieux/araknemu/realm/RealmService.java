package fr.quatrevieux.araknemu.realm;

import fr.quatrevieux.araknemu.core.BootException;
import fr.quatrevieux.araknemu.core.Service;
import fr.quatrevieux.araknemu.network.Server;
import org.apache.mina.core.service.IoHandler;

import java.io.IOException;

/**
 * Service for handling server authentication
 */
final public class RealmService implements Service {
    final private RealmConfiguration configuration;
    final private IoHandler ioHandler;

    private Server server;

    public RealmService(RealmConfiguration configuration, IoHandler ioHandler) {
        this.configuration = configuration;
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
            throw new BootException("Cannot start realm server", e);
        }
    }

    @Override
    public void shutdown() {
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public RealmConfiguration configuration() {
        return configuration;
    }

    public IoHandler ioHandler() {
        return ioHandler;
    }
}
