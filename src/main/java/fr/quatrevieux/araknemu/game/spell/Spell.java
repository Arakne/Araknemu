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

import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import org.checkerframework.checker.index.qual.Positive;

/**
 * Interface for spells
 */
public interface Spell extends Castable {
    /**
     * Get the spell id
     */
    public int id();

    /**
     * Get the spell sprite
     */
    public int spriteId();

    /**
     * Get the spell sprite arguments
     */
    public String spriteArgs();

    /**
     * Get the spell level in interval [1-6]
     */
    public @Positive int level();

    /**
     * Minimal player level for use the spell
     */
    public int minPlayerLevel();

    /**
     * Does critical failures will ends the current fight turn ?
     */
    public boolean endsTurnOnFailure();
}
