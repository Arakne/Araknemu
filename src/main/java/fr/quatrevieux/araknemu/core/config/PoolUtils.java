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

import fr.quatrevieux.araknemu.util.Asserter;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.common.value.qual.IntRange;

import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Define utility methods for Pool
 */
public final class PoolUtils implements Pool {
    private final Pool pool;

    public PoolUtils(Pool pool) {
        this.pool = pool;
    }

    @Override
    public boolean has(String key) {
        return pool.has(key);
    }

    @Override
    public @Nullable String get(String key) {
        return pool.get(key);
    }

    @Override
    public List<String> getAll(String key) {
        return pool.getAll(key);
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return pool.iterator();
    }

    /**
     * Parse a config item as integer
     */
    public int integer(String key, int defaultValue) {
        final String value = pool.get(key);

        return value != null
            ? Integer.parseInt(value)
            : defaultValue
        ;
    }

    /**
     * Parse a config item as a positive integer (i.e. >= 1)
     */
    public @Positive int positiveInteger(String key, @Positive int defaultValue) {
        return Asserter.assertPositive(integer(key, defaultValue));
    }

    /**
     * Parse a config item as a non-negative integer (i.e. >= 0)
     */
    public @NonNegative int nonNegativeInteger(String key, @NonNegative int defaultValue) {
        return Asserter.assertNonNegative(integer(key, defaultValue));
    }

    /**
     * Parse a config item as a percent integer (i.e. in interval [0, 100])
     */
    public @IntRange(from = 0, to = 100) int percent(String key, @IntRange(from = 0, to = 100) int defaultValue) {
        return Asserter.assertPercent(integer(key, defaultValue));
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
        String value = pool.get(key);

        if (value == null) {
            return defaultValue;
        }

        value = value.toLowerCase();

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
        final String value = pool.get(key);

        return value != null
            ? value
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
        final String value = pool.get(key);

        return value != null
            ? Double.parseDouble(value)
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
    @SuppressWarnings("argument") // because of toUpperCase() call, checker miss the value length
    public Duration duration(String key, Duration defaultValue) {
        String value = pool.get(key);

        if (value == null || value.isEmpty()) {
            return defaultValue;
        }

        value = value.toUpperCase();

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
