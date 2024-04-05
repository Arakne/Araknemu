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
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.core.config.env;

import fr.quatrevieux.araknemu.core.config.Pool;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.commons.lang3.tuple.Pair;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Handle interpolation of environment variables in a pool
 */
final class EnvPool implements Pool {
    private final Pool pool;
    private final Dotenv dotenv;
    private final Map<String, String> cache = new HashMap<>();

    public EnvPool(Pool pool, Dotenv dotenv) {
        this.pool = pool;
        this.dotenv = dotenv;
    }

    @Override
    public boolean has(String key) {
        return pool.has(key);
    }

    @Override
    public @Nullable String get(String key) {
        final @Nullable String cachedValue = cache.get(key);

        if (cachedValue != null) {
            return cachedValue;
        }

        final String rawValue = pool.get(key);

        if (rawValue == null) {
            return null;
        }

        final String value = interpolate(rawValue);

        cache.put(key, value);

        return value;
    }

    @Override
    public List<String> getAll(String key) {
        return pool.getAll(key).stream().map(this::interpolate).collect(Collectors.toList());
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return StreamSupport.stream(pool.spliterator(), false)
            .map(entry -> (Map.Entry<String, String>) Pair.of(entry.getKey(), interpolate(entry.getValue())))
            .iterator()
        ;
    }

    private String interpolate(String value) {
        return ExpressionParser.evaluate(value, dotenv::get);
    }
}
