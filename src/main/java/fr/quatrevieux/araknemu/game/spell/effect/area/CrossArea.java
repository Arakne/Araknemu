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

import fr.arakne.utils.maps.MapCell;
import fr.arakne.utils.maps.constant.Direction;
import fr.quatrevieux.araknemu.data.value.EffectArea;
import org.checkerframework.checker.index.qual.NonNegative;

import java.util.ArrayList;
import java.util.List;

/**
 * Resolver for cross area
 */
public final class CrossArea implements SpellEffectArea {
    private final EffectArea area;

    public CrossArea(EffectArea area) {
        this.area = area;
    }

    @Override
    public <C extends MapCell> List<C> resolve(C target, C source) {
        // Optimization for size 1
        if (size() == 1) {
            return Util.resolveCenterAndAdjacent(target);
        }

        final List<C> cells = new ArrayList<>(size() * 4 + 1);

        cells.add(target);

        for (Direction direction : Direction.values()) {
            if (!direction.restricted()) {
                continue;
            }

            LineArea.addCells(cells, target, direction, area.size());
        }

        Util.sortCells(target, cells);

        return cells;
    }

    @Override
    public EffectArea.Type type() {
        return EffectArea.Type.CROSS;
    }

    @Override
    public @NonNegative int size() {
        return area.size();
    }
}
