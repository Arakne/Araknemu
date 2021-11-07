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

package fr.quatrevieux.araknemu.realm;

import de.mkammerer.argon2.Argon2Factory;
import fr.quatrevieux.araknemu.core.config.ConfigurationModule;
import fr.quatrevieux.araknemu.core.config.Pool;
import fr.quatrevieux.araknemu.core.config.PoolUtils;
import fr.quatrevieux.araknemu.realm.authentication.password.Argon2Hash;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;

/**
 * Configuration for realm
 */
public final class RealmConfiguration implements ConfigurationModule {
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
     * The listened port for authentication server
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
     * Get the refresh interval for the ban ip table
     * Default: 30 seconds (30s)
     */
    public Duration banIpRefresh() {
        return pool.duration("banip.refresh", Duration.ofSeconds(30));
    }

    /**
     * Get list of enabled hash algorithms. The algorithms are separated by a coma.
     * Available algorithms : argon2, plain
     * The first value will be the default hash algorithm
     * Default value: "argon2, plain"
     */
    public String[] passwordHashAlgorithms() {
        final String[] algorithms = StringUtils.split(pool.string("password.defaultHash", "argon2, plain"), ",");

        for (int i = 0; i < algorithms.length; ++i) {
            algorithms[i] = algorithms[i].toLowerCase().trim();
        }

        return algorithms;
    }

    /**
     * The the argon2 configuration
     */
    public Argon2 argon2() {
        return new Argon2();
    }

    public final class Argon2 {
        /**
         * Get the number of iterations. Default: 4
         */
        public int iterations() {
            return pool.integer("password.argon2.iterations", 4);
        }

        /**
         * Get the memory, in kilobits. Default: 65536 (64Mo)
         */
        public int memory() {
            return pool.integer("password.argon2.memory", 64 * 1024);
        }

        /**
         * Get the parallelism parameter. Default: 8
         */
        public int parallelism() {
            return pool.integer("password.argon2.parallelism", 8);
        }

        /**
         * Get the argon2 algorithm type. Default: "argon2id"
         * Available types : "argon2i", "argon2d", "argon2id"
         */
        public Argon2Factory.Argon2Types type() {
            return Argon2Hash.typeByName(pool.string("password.argon2.type", "argon2id").toUpperCase());
        }
    }
}
