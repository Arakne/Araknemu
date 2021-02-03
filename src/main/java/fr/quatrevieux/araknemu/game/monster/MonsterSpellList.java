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

package fr.quatrevieux.araknemu.game.monster;

import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellList;

import java.util.Iterator;
import java.util.Map;

/**
 * Simple spell list for monster.
 *
 * This list is immutable, and spells cannot be upgraded nor boosted.
 * Because of its properties, this list can be shared between monster instances without conflicts.
 */
public final class MonsterSpellList implements SpellList {
    private final Map<Integer, Spell> spells;

    public MonsterSpellList(Map<Integer, Spell> spells) {
        this.spells = spells;
    }

    @Override
    public Spell get(int spellId) {
        return spells.get(spellId);
    }

    @Override
    public boolean has(int spellId) {
        return spells.containsKey(spellId);
    }

    @Override
    public Iterator<Spell> iterator() {
        return spells.values().iterator();
    }
}
