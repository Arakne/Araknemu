package fr.quatrevieux.araknemu.realm;

import fr.quatrevieux.araknemu.core.BootException;
import fr.quatrevieux.araknemu.core.Service;
import fr.quatrevieux.araknemu.network.adapter.Server;
import org.slf4j.Logger;

/**
 * Service for handling server authentication
 */
final public class RealmService implements Service {
    final private RealmConfiguration configuration;
    final private Server server;
    final private Logger logger;

    public RealmService(RealmConfiguration configuration, Server server, Logger logger) {
        this.configuration = configuration;
        this.server = server;
        this.logger = logger;
    }

    @Override
    public void boot() throws BootException {
        try {
            server.start();
        } catch (Exception e) {
            throw new BootException("Cannot start realm server", e);
        }
    }

    @Override
    public void shutdown() {
        logger.info("Stopping realm service...");

        try {
            server.stop();
        } catch (Exception e) {
            logger.error("Error during stopping the realm service", e);
        }

        logger.info("Realm service stopped");
    }

    public RealmConfiguration configuration() {
        return configuration;
    }
}
