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

package fr.quatrevieux.araknemu.game.player.characteristic;

import java.util.EnumMap;
import java.util.Map;

/**
 * Handle special effects
 */
public final class SpecialEffects {
    public enum Type {
        PODS,
        INITIATIVE,
        DISCERNMENT
    }

    private final Map<Type, Integer> map = new EnumMap<>(Type.class);

    /**
     * Add effect value to the map
     */
    public void add(Type type, int value) {
        map.put(type, map.getOrDefault(type, 0) + value);
    }

    /**
     * Subtract effect value to the map
     */
    public void sub(Type type, int value) {
        map.put(type, map.getOrDefault(type, 0) - value);
    }

    /**
     * Get the special effect value
     */
    public int get(Type type) {
        return map.getOrDefault(type, 0);
    }

    /**
     * Clear the special effects map
     */
    public void clear() {
        map.clear();
    }
}
