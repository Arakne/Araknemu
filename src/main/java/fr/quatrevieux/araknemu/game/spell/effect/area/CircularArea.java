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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.spell.effect.area;

import fr.arakne.utils.maps.CoordinateCell;
import fr.arakne.utils.maps.DofusMap;
import fr.arakne.utils.maps.MapCell;
import fr.quatrevieux.araknemu.data.value.EffectArea;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Area with circular form around center cell
 */
final public class CircularArea implements SpellEffectArea {
    final private EffectArea area;
    final private Predicate<Integer> distanceChecker;

    public CircularArea(EffectArea area, Predicate<Integer> distanceChecker) {
        this.area = area;
        this.distanceChecker = distanceChecker;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C extends MapCell> Set<C> resolve(C target, C source) {
        Set<C> cells = new HashSet<>();

        DofusMap<C> map = (DofusMap<C>) target.map();
        CoordinateCell<C> center = new CoordinateCell<>(target);

        for (int i = 0; i < map.size(); ++i) {
            CoordinateCell<C> cell = new CoordinateCell<>(map.get(i));

            if (distanceChecker.test(center.distance(cell))) {
                cells.add(cell.cell());
            }
        }

        return cells;
    }

    @Override
    public EffectArea.Type type() {
        return area.type();
    }

    @Override
    public int size() {
        return area.size();
    }
}
