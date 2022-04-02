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

package fr.quatrevieux.araknemu.game.spell;

import org.checkerframework.dataflow.qual.Pure;

/**
 * List of spells
 */
public interface SpellList extends Iterable<Spell> {
    /**
     * Get one spell by its id
     * {@link SpellList#has(int)} must be called before, and returns true. If not undefined behavior can occur
     *
     * @param spellId The spell id
     */
    public Spell get(int spellId);

    /**
     * Check if the creature are the spell
     *
     * @param spellId ID of the spell to check
     */
    @Pure
    public boolean has(int spellId);
}
