package fr.quatrevieux.araknemu.game;

import fr.quatrevieux.araknemu.core.config.ConfigurationModule;
import fr.quatrevieux.araknemu.core.config.Pool;
import fr.quatrevieux.araknemu.core.config.PoolUtils;

/**
 * Configuration class for game server
 */
final public class GameConfiguration implements ConfigurationModule {
    final public class PlayerConfiguration {
        /**
         * The player name regex
         */
        public String nameRegex() {
            return pool.string("player.name.regex", "[A-Z][a-z]+(-[A-Z]?[a-z]+)?");
        }

        /**
         * Minimal length of player name
         */
        public int nameMinLength() {
            return pool.integer("player.name.minLength", 2);
        }

        /**
         * Maximal length of player name
         */
        public int nameMaxLength() {
            return pool.integer("player.name.maxLength", 20);
        }

        /**
         * Maximum number for characters per account per server
         */
        public int maxPerAccount() {
            return pool.integer("player.max", 5);
        }
    }

    final public class ChatConfiguration {
        /**
         * Get the waiting time in seconds for global channel flood
         *
         * Set to -1 for deactivate
         */
        public int floodTime() {
            return pool.integer("chat.flood.time", 30);
        }
    }

    private PoolUtils pool;

    @Override
    public void setPool(Pool pool) {
        this.pool = new PoolUtils(pool);
    }

    @Override
    public String name() {
        return "game";
    }

    /**
     * Get the server ID. By default 1
     */
    public int id() {
        return pool.integer("id", 1);
    }

    /**
     * Get the server port
     */
    public int port() {
        return pool.integer("server.port", 5555);
    }

    /**
     * Get the server IP address
     */
    public String ip() {
        return pool.string("server.ip", "127.0.0.1");
    }

    /**
     * Get player configuration
     */
    public PlayerConfiguration player() {
        return new PlayerConfiguration();
    }

    /**
     * Get the chat configuration
     */
    public ChatConfiguration chat() {
        return new ChatConfiguration();
    }
}
