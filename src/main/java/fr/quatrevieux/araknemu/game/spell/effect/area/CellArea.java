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
import fr.quatrevieux.araknemu.data.value.EffectArea;
import org.checkerframework.checker.index.qual.NonNegative;

import java.util.Collections;
import java.util.Set;

/**
 * Resolve single cell
 */
public final class CellArea implements SpellEffectArea {
    public static final CellArea INSTANCE = new CellArea();

    @Override
    public <C extends MapCell> Set<C> resolve(C target, C source) {
        return Collections.singleton(target);
    }

    @Override
    public EffectArea.Type type() {
        return EffectArea.Type.CELL;
    }

    @Override
    public @NonNegative int size() {
        return 0;
    }
}
