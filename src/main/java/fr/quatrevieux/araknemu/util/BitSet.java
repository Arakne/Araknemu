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

package fr.quatrevieux.araknemu.util;

/**
 * Simple bit set using integer for storage
 *
 * @param <E> The set items
 */
final public class BitSet<E extends Enum> {
    private int value;

    /**
     * Set the item to the bit set
     *
     * @return true if the set has changed
     */
    public boolean set(E item) {
        final int oldValue = value;

        value |= 1 << item.ordinal();

        return oldValue != value;
    }

    /**
     * Remove the item from the bit set
     *
     * @return true if the set has changed
     */
    public boolean unset(E item) {
        final int oldValue = value;

        value &= ~(1 << item.ordinal());

        return oldValue != value;
    }

    /**
     * Check if the item is present in the set
     */
    public boolean check(E item) {
        final int mask = 1 << item.ordinal();

        return (value & mask) == mask;
    }

    /**
     * Get the bit set as integer value
     */
    public int toInt() {
        return value;
    }
}
