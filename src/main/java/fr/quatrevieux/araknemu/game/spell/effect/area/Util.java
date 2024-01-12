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

package fr.quatrevieux.araknemu.game.spell.effect.area;

import fr.arakne.utils.maps.CoordinateCell;
import fr.arakne.utils.maps.MapCell;

import java.util.Set;
import java.util.TreeSet;

/**
 * Utility methods for resolving effect areas
 */
final class Util {
    private Util() {
        // Disable constructor
    }

    /**
     * Create a set with cells ordered by distance from target cell
     */
    public static <C extends MapCell> Set<C> createOrderedSet(C target) {
        final CoordinateCell<C> center = target.coordinate();

        return new TreeSet<>((o1, o2) -> {
            final int firstDistance = center.distance(o1);
            final int secondDistance = center.distance(o2);

            if (firstDistance == secondDistance) {
                return o1.id() - o2.id();
            }

            return firstDistance - secondDistance;
        });
    }
}
