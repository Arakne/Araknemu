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

import java.time.Duration;

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

    /**
     * Parse a config item as integer
     */
    public int integer(String key, int defaultValue) {
        return pool.has(key)
            ? Integer.parseInt(pool.get(key))
            : defaultValue
        ;
    }

    /**
     * Parse a config item as integer
     * If the item is not configured, will return zero
     */
    public int integer(String key) {
        return integer(key, 0);
    }

    /**
     * Parse a config item as boolean
     * This method handle values like "1", "true", "yes", "on" as true
     */
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

    /**
     * Parse a config item as boolean
     * This method handle values like "1", "true", "yes", "on" as true
     * If the item is not configured, will return false
     */
    public boolean bool(String key) {
        return bool(key, false);
    }

    /**
     * Parse a config item as String
     */
    public String string(String key, String defaultValue) {
        return pool.has(key)
            ? pool.get(key)
            : defaultValue
        ;
    }

    /**
     * Parse a config item as String
     * If the item is not configured, will return an empty string
     */
    public String string(String key) {
        return string(key, "");
    }

    /**
     * Parse a config item as double
     */
    public double decimal(String key, double defaultValue) {
        return pool.has(key)
            ? Double.parseDouble(pool.get(key))
            : defaultValue
        ;
    }

    /**
     * Parse a config item as integer
     * If the item is not configured, will return zero
     */
    public double decimal(String key) {
        return decimal(key, 0d);
    }

    /**
     * Get a duration duration config item
     * Use the standard duration format (ISO-8601) with some differences :
     * - The string is case insensitive
     * - The prefix "PT" is not required
     *
     * Ex: "15s"    => 15 seconds
     *     "2m5s"   => 2 minutes and 5 seconds
     *     "p1dt5h" => 1 day and 5 hours
     *
     * @param key The config item key
     * @param defaultValue The value to use when the config item is not defined
     *
     * @return The parsed duration
     *
     * @see Duration#parse(CharSequence)
     */
    public Duration duration(String key, Duration defaultValue) {
        if (!pool.has(key)) {
            return defaultValue;
        }

        String value = pool.get(key).toUpperCase();

        if (value.charAt(0) != 'P') {
            value = "PT" + value;
        }

        return Duration.parse(value);
    }

    /**
     * Get a duration duration config item
     * Use the standard duration format (ISO-8601) with some differences :
     * - The string is case insensitive
     * - The prefix "PT" is not required
     *
     * Ex: "15s"    => 15 seconds
     *     "2m5s"   => 2 minutes and 5 seconds
     *     "p1dt5h" => 1 day and 5 hours
     *
     * @param key The config item key
     *
     * @return The parsed duration
     *
     * @see Duration#parse(CharSequence)
     */
    public Duration duration(String key) {
        return duration(key, Duration.ZERO);
    }
}
