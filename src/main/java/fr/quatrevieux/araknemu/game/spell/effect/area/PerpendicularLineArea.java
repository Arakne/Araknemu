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
 * Resolve perpendicular line (i.e. baton area)
 */
public final class PerpendicularLineArea implements SpellEffectArea {
    private final EffectArea area;

    public PerpendicularLineArea(EffectArea area) {
        this.area = area;
    }

    @Override
    public <C extends MapCell> List<C> resolve(C target, C source) {
        final List<C> cells = new ArrayList<>(1 + 2 * area.size());
        final Direction direction = source.coordinate().directionTo(target).orthogonal();

        cells.add(target);

        LineArea.addCells(cells, target, direction, area.size());
        LineArea.addCells(cells, target, direction.opposite(), area.size());

        Util.sortCells(target, cells);

        return cells;
    }

    @Override
    public EffectArea.Type type() {
        return EffectArea.Type.PERPENDICULAR_LINE;
    }

    @Override
    public @NonNegative int size() {
        return area.size();
    }
}
