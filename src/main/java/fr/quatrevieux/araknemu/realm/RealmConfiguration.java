package fr.quatrevieux.araknemu.realm;

import fr.quatrevieux.araknemu.core.config.ConfigurationModule;
import fr.quatrevieux.araknemu.core.config.Pool;
import fr.quatrevieux.araknemu.core.config.PoolUtils;

/**
 * Configuration for realm
 */
final public class RealmConfiguration implements ConfigurationModule {
    private PoolUtils pool;

    @Override
    public void setPool(Pool pool) {
        this.pool = new PoolUtils(pool);
    }

    @Override
    public String name() {
        return "realm";
    }

    /**
     * The listner port for server
     */
    public int port() {
        return pool.integer("server.port", 444);
    }

    /**
     * Get the required Dofus client version for logged in
     */
    public String clientVersion() {
        return pool.string("client.version", "1.29.1");
    }
}
