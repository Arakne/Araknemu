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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game;

import fr.quatrevieux.araknemu.core.config.ConfigurationModule;
import fr.quatrevieux.araknemu.core.config.Pool;
import fr.quatrevieux.araknemu.core.config.PoolUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.common.value.qual.IntRange;

import java.time.Duration;
import java.time.ZoneId;
import java.util.Arrays;

/**
 * Configuration class for game server
 */
public final class GameConfiguration {
    public static final ConfigurationModule<GameConfiguration> MODULE = new ConfigurationModule<GameConfiguration>() {
        @Override
        public GameConfiguration create(Pool pool) {
            return new GameConfiguration(pool);
        }

        @Override
        public String name() {
            return "game";
        }
    };

    private final PoolUtils pool;

    public GameConfiguration(Pool pool) {
        this.pool = new PoolUtils(pool);
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
     * Get the configured time zone.
     * Default: system default time zone
     */
    public ZoneId timezone() {
        return pool.zoneId("server.timezone", ZoneId.systemDefault());
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
     * Get the shutdown reminder delays, in minutes
     * The values are integer separated by a comma ","
     * Default value : "1,10,30,60,120"
     */
    public long[] shutdownReminderMinutes() {
        return Arrays.stream(StringUtils.split(pool.string("shutdownReminderMinutes", "1,10,30,60,120"), ","))
            .map(String::trim)
            .mapToLong(Long::parseLong)
            .sorted()
            .toArray()
        ;
    }

    /**
     * Get the refresh interval for the ban ip table
     * Default: 10 minutes (10m)
     */
    public Duration banIpRefresh() {
        return pool.duration("banip.refresh", Duration.ofMinutes(10));
    }

    /**
     * The interval between two automatic save of connected players
     * Note: Players are regularly saved apart of autosave, so it's not required to set a small value
     * Default: 4 hours (4h)
     */
    public Duration autosaveInterval() {
        return pool.duration("autosave.interval", Duration.ofHours(4));
    }

    /**
     * Enable automatic saving of connected players
     * Default: true
     */
    public boolean autosaveEnabled() {
        return pool.bool("autosave.enabled", true);
    }

    /**
     * Does preload is enabled for the given service
     * By default this value is true
     *
     * In case of submodule (i.e. preload.service.submodule), the preload will be checked on each part.
     * So if "preload.service" is false, "preload.service.submodule" will be considered as false
     *
     * @param service The service name
     *
     * @return true if preload is enabled
     *
     * @see PreloadableService#name() For the name of the server
     */
    public boolean preload(String service) {
        final String key = "preload." + service;

        for (int pos = key.indexOf('.'); pos != -1; pos = key.indexOf('.', pos + 1)) {
            if (!pool.bool(key.substring(0, pos), true)) {
                return false;
            }
        }

        return pool.bool(key, true);
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

    /**
     * Get the configuration for the fight system
     */
    public FightConfiguration fight() {
        return new FightConfiguration();
    }

    public final class PlayerConfiguration {
        /**
         * The player name regex
         */
        public String nameRegex() {
            return pool.string("player.name.regex", "[A-Z][a-z]+(-[A-Z]?[a-z]+)?");
        }

        /**
         * Minimal length of player name
         */
        public @Positive int nameMinLength() {
            return pool.positiveInteger("player.name.minLength", 2);
        }

        /**
         * Maximal length of player name
         */
        public @Positive int nameMaxLength() {
            return pool.positiveInteger("player.name.maxLength", 20);
        }

        /**
         * Maximal length for generated name
         */
        public @Positive int maxNameGeneratedLength() {
            return pool.positiveInteger("player.name.generated.maxLength", 8);
        }

        /**
         * Minimal length for generated name
         */
        public @Positive int minNameGeneratedLength() {
            return pool.positiveInteger("player.name.generated.minLength", 4);
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
        public @Positive int deleteAnswerLevel() {
            return pool.positiveInteger("player.deleteAnswerLevel", 20);
        }

        /**
         * Get the level up spell points
         * By default, value is set to 1
         */
        public @NonNegative int spellBoostPointsOnLevelUp() {
            return pool.nonNegativeInteger("player.level.spellPoints", 1);
        }

        /**
         * Get the level up characteristic points
         * By default, value is set to 5
         */
        public @NonNegative int characteristicPointsOnLevelUp() {
            return pool.nonNegativeInteger("player.level.characteristicPoints", 5);
        }

        /**
         * The life regeneration rate.
         * This is the number of milliseconds required to regenerate 1 life point. Set to 0 to disable.
         * By default 1000 (1 LP / sec)
         */
        public @NonNegative int baseLifeRegeneration() {
            return pool.nonNegativeInteger("player.lifeRegeneration.base", 1000);
        }

        /**
         * Restore life points when player reach a new level
         * By default true
         */
        public boolean restoreLifeOnLevelUp() {
            return pool.bool("player.restoreLifeOnLevelUp", true);
        }
    }

    public final class ChatConfiguration {
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

        /**
         * Interval of time for checking spam of messages or smileys
         * During this interval, the player can send a limited number of messages or smileys
         * Once it reached, the player should wait the end of the interval for send a new message
         * By default 30s
         */
        public Duration spamCheckInterval() {
            return pool.duration("chat.spam.interval", Duration.ofSeconds(30));
        }

        /**
         * Maximum number of messages or smileys per spam check interval
         * This value must be a positive integer (> 1)
         * By default 5
         */
        public @Positive int spamCheckMaxCount() {
            return pool.positiveInteger("chat.spam.max", 5);
        }
    }

    public final class ActivityConfiguration {
        /**
         * Number of threads to use for the activity service
         */
        public @Positive int threadsCount() {
            return pool.positiveInteger("activity.threadsCount", 1);
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
        public @IntRange(from = 0, to = 100) int monsterMovePercent() {
            return pool.percent("activity.monsters.movePercent", 25);
        }

        /**
         * The maximum move distance for monsters
         * By default 5
         */
        public @Positive int monsterMoveDistance() {
            return pool.positiveInteger("activity.monsters.moveDistance", 5);
        }

        /**
         * The delay divisor for respawn a monster group
         * With a factor of 2, the respawn will be 2 times faster
         * By default 1
         */
        public @Positive int monsterRespawnSpeedFactor() {
            return pool.positiveInteger("activity.monsters.respawnSpeedFactor", 1);
        }
    }

    public final class EconomyConfiguration {
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

    public final class FightConfiguration {
        /**
         * The threads count for run fight actions and AI
         * This value should be greater than 2. A good value may be around 1 thread per 100 fights
         * By default, 4
         */
        public int threadsCount() {
            return pool.integer("fight.threadsCount", 4);
        }

        /**
         * The fight turn duration
         * The value should be a duration string like 30s, 1m10s
         * Default value : 30s
         */
        public Duration turnDuration() {
            return pool.duration("fight.turnDuration", Duration.ofSeconds(30));
        }

        /**
         * The placement duration for a PVM fight
         * The value should be a duration string like 30s, 1m10s
         * Default value : 45s
         */
        public Duration pvmPlacementDuration() {
            return pool.duration("fight.pvm.placementDuration", Duration.ofSeconds(45));
        }

        /**
         * Get the XP multiplier
         * The value should be a positive decimal number.
         * Default value : 1.0
         */
        public double xpRate() {
            return pool.decimal("fight.rate.xp", 1.0);
        }

        /**
         * Get the drop rate multiplier
         * The value should be a positive decimal number.
         * This value will modify the object drop chance, but not the maximum dropped items per monster
         * nor minimal discernment requirement.
         * Default value : 1.0
         */
        public double dropRate() {
            return pool.decimal("fight.rate.drop", 1.0);
        }
    }
}
