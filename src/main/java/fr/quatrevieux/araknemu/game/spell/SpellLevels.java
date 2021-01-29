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

import fr.quatrevieux.araknemu.data.world.entity.SpellTemplate;

import java.util.NoSuchElementException;

/**
 * Wrap spell levels
 */
final public class SpellLevels {
    final private SpellTemplate entity;
    final private Spell[] levels;

    public SpellLevels(SpellTemplate entity, Spell[] levels) {
        this.entity = entity;
        this.levels = levels;
    }

    /**
     * Get the spell id
     */
    public int id() {
        return entity.id();
    }

    /**
     * Get the spell name
     */
    public String name() {
        return entity.name();
    }

    /**
     * Get spell at level
     */
    public Spell level(int level) {
        --level;

        if (level < 0 || level >= levels.length) {
            throw new NoSuchElementException("Invalid level " + (level + 1) + " for spell " + entity.id());
        }

        return levels[level];
    }

    /**
     * Get the maximum spell level
     */
    public int max() {
        return levels.length;
    }
}
