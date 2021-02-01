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

import fr.quatrevieux.araknemu.game.spell.Spell;

import java.util.Collection;

/**
 * Handle spells boosts effects
 */
public interface SpellsBoosts {
    public static enum Modifier {
        RANGE(281),
        MODIFIABLE_RANGE(282),
        DAMAGE(283),
        HEAL(284),
        AP_COST(285),
        REDUCE_DELAY(286),
        CRITICAL(287),
        LAUNCH_LINE(288),
        LINE_OF_SIGHT(289),
        LAUNCH_PER_TURN(290),
        LAUNCH_PER_TARGET(291),
        SET_DELAY(292);

        private final int effectId;

        Modifier(int effectId) {
            this.effectId = effectId;
        }

        public int effectId() {
            return effectId;
        }
    }

    /**
     * Boost the spell
     *
     * @param spellId The spell to boost
     * @param modifier The effect modifier
     * @param value The boosted value
     *
     * @return The new boosted value
     */
    public int boost(int spellId, Modifier modifier, int value);

    /**
     * Set the modifier value
     *
     * @param spellId The spell to modify
     * @param modifier The spell modifier
     * @param value The new value
     *
     * @return The new value
     */
    public int set(int spellId, Modifier modifier, int value);

    /**
     * Remove the modifier
     *
     * @param spellId Spell
     * @param modifier The modifier
     */
    public void unset(int spellId, Modifier modifier);

    /**
     * Get spell modifiers for the spell id
     *
     * @param spellId The spell to check
     */
    public SpellModifiers modifiers(int spellId);

    /**
     * Get the boosted spell
     *
     * @param spell spell to boost
     */
    public Spell get(Spell spell);

    /**
     * Get all spells modifiers
     */
    public Collection<SpellModifiers> all();
}
