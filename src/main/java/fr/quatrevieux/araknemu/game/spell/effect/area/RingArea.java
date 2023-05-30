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

import java.util.Set;

/**
 * Resolve ring area (circle border)
 */
public final class RingArea implements SpellEffectArea {
    private final CircularArea area;

    public RingArea(EffectArea area) {
        this.area = new CircularArea(area, distance -> distance == area.size());
    }

    @Override
    public <C extends MapCell> Set<C> resolve(C target, C source) {
        return area.resolve(target, source);
    }

    @Override
    public EffectArea.Type type() {
        return area.type();
    }

    @Override
    public @NonNegative int size() {
        return area.size();
    }
}
