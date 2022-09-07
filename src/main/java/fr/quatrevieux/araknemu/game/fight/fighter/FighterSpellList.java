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
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.fighter;

import fr.quatrevieux.araknemu.game.spell.SpellList;
import fr.quatrevieux.araknemu.game.spell.boost.SpellsBoosts;

/**
 * Base type for handle spell list of a fighter
 * Add support of spell boost
 */
public interface FighterSpellList extends SpellList {
    /**
     * Boost the spell
     *
     * @param spellId The spell to boost
     * @param modifier The effect modifier
     * @param value The boosted value. Use a negative value for remove the boost.
     */
    public void boost(int spellId, SpellsBoosts.Modifier modifier, int value);
}
