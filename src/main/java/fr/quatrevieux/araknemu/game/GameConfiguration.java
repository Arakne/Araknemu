/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game;

import fr.quatrevieux.araknemu.core.config.ConfigurationModule;
import fr.quatrevieux.araknemu.core.config.Pool;
import fr.quatrevieux.araknemu.core.config.PoolUtils;

import java.time.Duration;

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

    final public class EconomyConfiguration{
        /**
         * Get the selling item to an NPC price multiplier
         * By default 0.1
         *
         * Should corresponds to "C.SELL_PRICE_MULTIPLICATOR" in lang_xx_xxx.swf
         */
        public double npcSellPriceMultiplier() {
            return pool.decimal("economy.npc.sellPriceMultiplier", .1d);
        }

        /**
         * The bank cost per item entries
         * The value must be a positive double
         * Default to 1
         * Set to 0 to disable the bank cost
         */
        public double bankCostPerEntry() {
            return pool.decimal("economy.bank.costPerEntry", 1);
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
     * The maximum inactivity time, as duration
     * By default, 15min ("PT15M" or "15m")
     */
    public Duration inactivityTime() {
        return pool.duration("inactivityTime", Duration.ofMinutes(15));
    }

    /**
     * Maximum number of received packets per seconds per clients
     * When the limit is reached, the client session is closed
     */
    public int packetRateLimit() {
        return pool.integer("packetRateLimit", 100);
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

    /**
     * Get the configuration for the Dofus economy
     */
    public EconomyConfiguration economy() {
        return new EconomyConfiguration();
    }
}
