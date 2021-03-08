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

package fr.quatrevieux.araknemu.game.spell.boost;

import java.util.Map;

/**
 * Implements {@link SpellModifiers} using map
 */
public final class MapSpellModifiers implements SpellModifiers {
    private final int spellId;
    private final Map<SpellsBoosts.Modifier, Integer> modifiers;

    public MapSpellModifiers(int spellId, Map<SpellsBoosts.Modifier, Integer> modifiers) {
        this.spellId = spellId;
        this.modifiers = modifiers;
    }

    @Override
    public int spellId() {
        return spellId;
    }

    @Override
    public int value(SpellsBoosts.Modifier modifier) {
        return modifiers.getOrDefault(modifier, 0);
    }

    @Override
    public boolean has(SpellsBoosts.Modifier modifier) {
        return modifiers.containsKey(modifier);
    }
}
