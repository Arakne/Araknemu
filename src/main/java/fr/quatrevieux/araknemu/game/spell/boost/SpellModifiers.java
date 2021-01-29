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

/**
 * Container for spell boosts modifiers
 */
public interface SpellModifiers {
    /**
     * Get the modified spell id
     */
    public int spellId();

    /**
     * Get the boost value
     *
     * @param modifier The spell modifier
     */
    public int value(SpellsBoosts.Modifier modifier);

    /**
     * Check if the modifier is present
     *
     * @param modifier The spell modifier to check
     */
    public boolean has(SpellsBoosts.Modifier modifier);

    /**
     * Get the range modifier
     */
    default public int range() {
        return value(SpellsBoosts.Modifier.RANGE);
    }

    /**
     * Check the spell modifiable range modifier
     */
    default public boolean modifiableRange() {
        return value(SpellsBoosts.Modifier.MODIFIABLE_RANGE) > 0;
    }

    /**
     * Get the damage boost
     */
    default public int damage() {
        return value(SpellsBoosts.Modifier.DAMAGE);
    }

    /**
     * Get the heal boost
     */
    default public int heal() {
        return value(SpellsBoosts.Modifier.HEAL);
    }

    /**
     * Get the AP cost modifier
     */
    default public int apCost() {
        return value(SpellsBoosts.Modifier.AP_COST);
    }

    /**
     * Get the launch delay reduce
     */
    default public int delay() {
        return value(SpellsBoosts.Modifier.REDUCE_DELAY);
    }

    /**
     * Check if the a fixed delay modifier is present
     */
    default public boolean hasFixedDelay() {
        return has(SpellsBoosts.Modifier.SET_DELAY);
    }

    /**
     * Get the fixed delay value
     */
    default public int fixedDelay() {
        return value(SpellsBoosts.Modifier.SET_DELAY);
    }

    /**
     * Check if can launch spell out of line
     */
    default public boolean launchOutline() {
        return value(SpellsBoosts.Modifier.LAUNCH_LINE) > 0;
    }

    /**
     * Check if can launch ignoring line of sight
     */
    default public boolean lineOfSight() {
        return value(SpellsBoosts.Modifier.LINE_OF_SIGHT) > 0;
    }

    /**
     * Get the launch per target modifier
     */
    default public int launchPerTarget() {
        return value(SpellsBoosts.Modifier.LAUNCH_PER_TARGET);
    }

    /**
     * Get the launch per turn modifier
     */
    default public int launchPerTurn() {
        return value(SpellsBoosts.Modifier.LAUNCH_PER_TURN);
    }

    /**
     * Get the critical hit rate modifier
     */
    default public int criticalHit() {
        return value(SpellsBoosts.Modifier.CRITICAL);
    }
}
