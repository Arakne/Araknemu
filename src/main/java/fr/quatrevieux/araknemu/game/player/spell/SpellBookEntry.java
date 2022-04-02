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

package fr.quatrevieux.araknemu.game.player.spell;

import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.game.player.spell.event.SpellMoved;
import fr.quatrevieux.araknemu.game.player.spell.event.SpellUpgraded;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellLevels;
import org.checkerframework.common.value.qual.IntRange;
import org.checkerframework.dataflow.qual.Pure;

/**
 * Entry for the spell book
 */
public final class SpellBookEntry {
    private final SpellBook spellBook;
    private final PlayerSpell entity;
    private final SpellLevels spell;

    public SpellBookEntry(SpellBook spellBook, PlayerSpell entity, SpellLevels spell) {
        this.spellBook = spellBook;
        this.entity = entity;
        this.spell = spell;
    }

    /**
     * Get the spell
     */
    @Pure
    public Spell spell() {
        return spell.level(entity.level());
    }

    /**
     * Get the spell position
     */
    @Pure
    public @IntRange(from = 1, to = 63) int position() {
        return entity.position();
    }

    /**
     * This entry is a class spell ?
     */
    @Pure
    public boolean classSpell() {
        return entity.classSpell();
    }

    /**
     * Move the spell to a new position
     *
     * @param position The target position
     */
    public void move(@IntRange(from = 1, to = 63) int position) {
        if (position < 1) {
            throw new IllegalArgumentException("Bad position");
        }

        spellBook.freePosition(this);
        entity.setPosition(position);
        spellBook.indexing(this);

        spellBook.dispatch(new SpellMoved(this));
    }

    /**
     * Upgrade the spell to the next level
     */
    public void upgrade() {
        if (entity.level() == spell.max()) {
            throw new IllegalStateException("Maximum spell level reached");
        }

        final int nextLevel = entity.level() + 1;
        final Spell nextSpell = spell.level(nextLevel);

        if (!spellBook.canUpgrade(nextSpell)) {
            throw new IllegalStateException("Cannot upgrade spell");
        }

        spellBook.removePointsForUpgrade(nextSpell);
        entity.setLevel(nextLevel);

        spellBook.dispatch(new SpellUpgraded(this));
    }

    /**
     * Get the entity
     *
     * /!\ Internal method for listeners
     */
    @Pure
    public PlayerSpell entity() {
        return entity;
    }
}
