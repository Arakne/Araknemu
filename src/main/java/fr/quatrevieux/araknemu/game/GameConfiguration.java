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
         * Maximal length for generated name
         */
        public int maxNameGeneratedLength() {
            return pool.integer("player.name.generated.maxLength", 8);
        }

        /**
         * Minimal length for generated name
         */
        public int minNameGeneratedLength() {
            return pool.integer("player.name.generated.minLength", 4);
        }

        /**
         * Maximum number for characters per account per server
         */
        public int maxPerAccount() {
            return pool.integer("player.max", 5);
        }

        /**
         * Minimum level which needs secret answer for delete the character
         * By default, value is set to 20
         *
         * To change this value, you should also change in lang.swf, the value `C.SECRET_ANSWER_SINCE_LEVEL`
         */
        public int deleteAnswerLevel() {
            return pool.integer("player.deleteAnswerLevel", 20);
        }

        /**
         * Get the level up spell points
         * By default, value is set to 1
         */
        public int spellBoostPointsOnLevelUp() {
            return pool.integer("player.level.spellPoints", 1);
        }

        /**
         * Get the level up characteristic points
         * By default, value is set to 5
         */
        public int characteristicPointsOnLevelUp() {
            return pool.integer("player.level.characteristicPoints", 5);
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

        /**
         * Get list of default channels to add on character creation
         */
        public String defaultChannels() {
            return pool.string("chat.channels.default", "*#%!pi$:?");
        }

        /**
         * Channels to add on admin characters
         */
        public String adminChannels() {
            return pool.string("chat.channels.admin", "@");
        }
    }

    final public class ActivityConfiguration {
        /**
         * Number of threads to use for the activity service
         */
        public int threadsCount() {
            return pool.integer("activity.threadsCount", 1);
        }

        /**
         * Number of seconds for move monster groups
         * By default 120s = 2min
         */
        public int monsterMoveInterval() {
            return pool.integer("activity.monsters.moveInterval", 120);
        }

        /**
         * Percent of chance that a monster group on a map move
         * The value must be an integer value between ]0, 100]
         * By default 25%
         */
        public int monsterMovePercent() {
            return pool.integer("activity.monsters.movePercent", 25);
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
     * The bank cost per item entries
     * The value must be a positive integer
     * Default to 1
     * Set to 0 to disable the bank cost
     */
    public int bankCostPerEntry() {
        return pool.integer("bank.costPerEntry", 1);
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

    /**
     * Get the configuration for the game activity
     */
    public ActivityConfiguration activity() {
        return new ActivityConfiguration();
    }
}
