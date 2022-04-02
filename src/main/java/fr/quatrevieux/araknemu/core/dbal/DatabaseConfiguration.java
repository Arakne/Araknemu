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

package fr.quatrevieux.araknemu.core.dbal;

import fr.quatrevieux.araknemu.core.config.ConfigurationModule;
import fr.quatrevieux.araknemu.core.config.Pool;
import fr.quatrevieux.araknemu.core.config.PoolUtils;

/**
 * Configuration module for database system
 */
public final class DatabaseConfiguration {
    public static final ConfigurationModule<DatabaseConfiguration> MODULE = new ConfigurationModule<DatabaseConfiguration>() {
        @Override
        public DatabaseConfiguration create(Pool pool) {
            return new DatabaseConfiguration(pool);
        }

        @Override
        public String name() {
            return "database";
        }
    };

    private final PoolUtils pool;

    public DatabaseConfiguration(Pool pool) {
        this.pool = new PoolUtils(pool);
    }

    /**
     * Get a connection configuration
     *
     * @param name The connection name
     */
    public Connection connection(String name) {
        return new Connection(name, pool);
    }

    public static final class Connection {
        private final String name;
        private final PoolUtils pool;

        public Connection(String name, PoolUtils pool) {
            this.name = name;
            this.pool = pool;
        }

        public String name() {
            return name;
        }

        /**
         * Get the connection host (useful for MySQL)
         */
        public String host() {
            return pool.string(name + ".host", "127.0.0.1");
        }

        /**
         * Get the username
         */
        public String user() {
            return pool.string(name + ".user", "root");
        }

        /**
         * Get the user password
         */
        public String password() {
            return pool.string(name + ".password");
        }

        /**
         * Get the database name (by default, same as connection name)
         */
        public String dbname() {
            return pool.string(name + ".dbname", name);
        }

        /**
         * Get the database type name
         * Can be "mysql" or "sqlite"
         */
        public String type() {
            return pool.string(name + ".type", "mysql");
        }

        /**
         * Is a memory connection (sqlite)
         */
        public boolean memory() {
            return pool.bool(name + ".memory");
        }

        /**
         * Shared cache (sqlite)
         * Useful for SQLite in-memory, for sharing data between connections
         */
        public boolean shared() {
            return pool.bool(name + ".shared", true);
        }

        /**
         * Get the maximum pool size
         */
        public int maxPoolSize() {
            return pool.integer(name + ".poolSize", 16);
        }

        /**
         * Get the database file path (sqlite)
         * By default same as {@link Connection#dbname()} with .db extension
         */
        public String path() {
            return pool.string(name + ".path", dbname() + ".db");
        }

        /**
         * Auto-reconnect to the connection when query fail
         * Default to true
         */
        public boolean autoReconnect() {
            return pool.bool(name + ".autoReconnect", true);
        }

        /**
         * Get the refresh pool interval in seconds
         * If the value is lower than 1, the refresh will be disabled
         * Default to 3600 (1 hour)
         */
        public int refreshPoolInterval() {
            return pool.integer(name + ".refreshPoolInterval", 3600);
        }
    }
}
