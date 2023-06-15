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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.fighter;

import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.boost.SpellsBoosts;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Null object for {@link FighterSpellList}
 */
public final class EmptySpellList implements FighterSpellList {
    public static final EmptySpellList INSTANCE = new EmptySpellList();

    @Override
    public void boost(int spellId, SpellsBoosts.Modifier modifier, int value) {
        // do nothing
    }

    @Override
    public Spell get(int spellId) {
        throw new NoSuchElementException();
    }

    @Override
    public boolean has(int spellId) {
        return false;
    }

    @Override
    public Iterator<Spell> iterator() {
        return Collections.emptyIterator();
    }
}
