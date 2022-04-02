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

package fr.quatrevieux.araknemu.data.value;

import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.Signed;
import org.checkerframework.dataflow.qual.Pure;

/**
 * Position object into the world
 */
public final class Position {
    private final @NonNegative int map;
    private final @NonNegative int cell;

    public Position(@NonNegative int map, @NonNegative int cell) {
        this.map = map;
        this.cell = cell;
    }

    @Pure
    public @NonNegative int map() {
        return map;
    }

    @Pure
    public @NonNegative int cell() {
        return cell;
    }

    @Pure
    public boolean isNull() {
        return map == 0 && cell == 0;
    }

    /**
     * Change the cell position
     */
    public Position newCell(@NonNegative int cell) {
        return new Position(map, cell);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        return
            this == o
            || (o instanceof Position && equals((Position) o))
        ;
    }

    public boolean equals(@Nullable Position other) {
        return other != null && other.cell == cell && other.map == map;
    }

    @Override
    public int hashCode() {
        @Signed int result = map;
        result = 31 * result + cell;
        return result;
    }

    @Override
    public String toString() {
        return "(" + map + ", " + cell + ")";
    }
}
