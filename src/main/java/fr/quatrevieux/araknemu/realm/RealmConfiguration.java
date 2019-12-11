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

import fr.quatrevieux.araknemu.core.config.ConfigurationModule;
import fr.quatrevieux.araknemu.core.config.Pool;
import fr.quatrevieux.araknemu.core.config.PoolUtils;

import java.time.Duration;

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

    /**
     * The maximum inactivity time, as duration
     * By default, 15min ("PT15M" or "15m")
     */
    public Duration inactivityTime() {
        return pool.duration("inactivityTime", Duration.ofMinutes(15));
    }
}
