package fr.quatrevieux.araknemu.realm;

import fr.quatrevieux.araknemu.core.BootException;
import fr.quatrevieux.araknemu.core.Service;
import fr.quatrevieux.araknemu.network.adapter.Server;

import java.io.IOException;

/**
 * Service for handling server authentication
 */
final public class RealmService implements Service {
    final private RealmConfiguration configuration;
    final private Server server;

    public RealmService(RealmConfiguration configuration, Server server) {
        this.configuration = configuration;
        this.server = server;
    }

    @Override
    public void boot() throws BootException {
        try {
            server.start();
        } catch (IOException e) {
            throw new BootException("Cannot start realm server", e);
        }
    }

    @Override
    public void shutdown() {
        try {
            server.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public RealmConfiguration configuration() {
        return configuration;
    }
}
