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

package fr.quatrevieux.araknemu.game.world.creature.characteristics;

import fr.quatrevieux.araknemu.data.constant.Characteristic;

import java.util.EnumMap;
import java.util.Map;

/**
 * Simple implementation for characteristics map
 */
public final class DefaultCharacteristics implements MutableCharacteristics {
    private final Map<Characteristic, Integer> map = new EnumMap<>(Characteristic.class);

    @Override
    public int get(Characteristic characteristic) {
        return map.getOrDefault(characteristic, 0);
    }

    @Override
    public void set(Characteristic characteristic, int value) {
        map.put(characteristic, value);
    }

    @Override
    public void add(Characteristic characteristic, int value) {
        map.put(characteristic, map.getOrDefault(characteristic, 0) + value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof Characteristics) {
            return equals((Characteristics) obj);
        }

        return false;
    }

    /**
     * Check equality from two abstract characteristics map
     *
     * Two characteristics map are equals if and only if all characteristic values are equals
     */
    public boolean equals(Characteristics other) {
        for (Characteristic characteristic : Characteristic.values()) {
            if (get(characteristic) != other.get(characteristic)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int h = 0;

        for (Characteristic characteristic : Characteristic.values()) {
            final int value = get(characteristic);

            if (value != 0) {
                h += characteristic.hashCode() ^ value;
            }
        }

        return h;
    }
}
