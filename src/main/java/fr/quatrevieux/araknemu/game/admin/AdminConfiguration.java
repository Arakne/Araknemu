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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.admin;

import fr.quatrevieux.araknemu.core.config.ConfigurationModule;
import fr.quatrevieux.araknemu.core.config.Pool;
import fr.quatrevieux.araknemu.core.config.PoolUtils;

/**
 * Configuration for admin system
 */
public final class AdminConfiguration implements ConfigurationModule {
    private PoolUtils pool;

    @Override
    public void setPool(Pool pool) {
        this.pool = new PoolUtils(pool);
    }

    @Override
    public String name() {
        return "admin";
    }

    /**
     * Get a command context configuration
     *
     * @param name The context name
     */
    public ContextConfiguration context(String name) {
        return new ContextConfiguration(name);
    }

    class ContextConfiguration {
        private final String name;

        public ContextConfiguration(String name) {
            this.name = name;
        }

        /**
         * Does scripts are enable
         * If set to false, the scripts are not loaded
         * Default value: true
         */
        public boolean enableScripts() {
            return pool.bool(name + ".scripts.enable", true);
        }

        /**
         * Get the commands scripts path
         * Should be a directory path
         * Default: "scripts/commands/[context]"
         */
        public String scriptsPath() {
            return pool.string(name + ".scripts.path", "scripts/commands/" + name);
        }
    }
}
