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

package fr.quatrevieux.araknemu.core.config;

/**
 * Define utility methods for Pool
 */
final public class PoolUtils implements Pool {
    final private Pool pool;

    public PoolUtils(Pool pool) {
        this.pool = pool;
    }

    @Override
    public boolean has(String key) {
        return pool.has(key);
    }

    @Override
    public String get(String key) {
        return pool.get(key);
    }

    public int integer(String key, int defaultValue) {
        return pool.has(key)
            ? Integer.parseInt(pool.get(key))
            : defaultValue
        ;
    }

    public int integer(String key) {
        return integer(key, 0);
    }

    public boolean bool(String key, boolean defaultValue) {
        if (!pool.has(key)) {
            return defaultValue;
        }

        String value = pool.get(key).toLowerCase();

        for (String v : new String[] {"1", "true", "yes", "on"}) {
            if (value.equals(v)) {
                return true;
            }
        }

        return false;
    }

    public boolean bool(String key) {
        return bool(key, false);
    }

    public String string(String key, String defaultValue) {
        return pool.has(key)
            ? pool.get(key)
            : defaultValue
        ;
    }

    public String string(String key) {
        return string(key, "");
    }

    public double decimal(String key, double defaultValue) {
        return pool.has(key)
            ? Double.parseDouble(pool.get(key))
            : defaultValue
        ;
    }

    public double decimal(String key) {
        return decimal(key, 0d);
    }
}
