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

package fr.quatrevieux.araknemu.data.world.transformer;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.transformer.TransformerException;
import fr.quatrevieux.araknemu.util.Asserter;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.PolyNull;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Transform weapons abilities JSON object to map
 * The JSON object keys are the weapon IDs and the values are the abilities in percent
 */
public final class WeaponsAbilitiesTransformer implements Transformer<Map<Integer, @NonNegative Integer>> {
    private final Gson gson;
    private final Type jsonType = new TypeToken<Map<Integer, Integer>>() { }.getType();

    public WeaponsAbilitiesTransformer(Gson gson) {
        this.gson = gson;
    }

    public WeaponsAbilitiesTransformer() {
        this(new Gson());
    }

    @Override
    public @PolyNull String serialize(@PolyNull Map<Integer, @NonNegative Integer> value) {
        if (value == null) {
            return null;
        }

        return gson.toJson(value, jsonType);
    }

    @Override
    public @PolyNull Map<Integer, @NonNegative Integer> unserialize(@PolyNull String serialize) throws TransformerException {
        if (serialize == null) {
            return null;
        }

        final Map<Integer, Integer> map;

        try {
            map = gson.fromJson(serialize, jsonType);
        } catch (JsonSyntaxException e) {
            throw new TransformerException("Invalid JSON : " + serialize, e);
        }

        if (map == null) {
            throw new TransformerException("Null or empty value is not allowed");
        }

        final Map<Integer, @NonNegative Integer> ret = new HashMap<>();

        try {
            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                ret.put(entry.getKey(), Asserter.assertNonNegative(entry.getValue()));
            }
        } catch (IllegalArgumentException e) {
            throw new TransformerException("Invalid JSON value : " + serialize, e);
        }

        return Collections.unmodifiableMap(ret);
    }
}
