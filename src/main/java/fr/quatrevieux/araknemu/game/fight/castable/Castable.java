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

package fr.quatrevieux.araknemu.game.fight.castable;

import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import org.checkerframework.checker.index.qual.NonNegative;

import java.util.List;

/**
 * Object which can be casted on a fight
 */
public interface Castable {
    /**
     * List of normal spell effects
     */
    public List<SpellEffect> effects();

    /**
     * List of critical spell effects
     */
    public List<SpellEffect> criticalEffects();

    /**
     * The AP cost
     */
    public @NonNegative int apCost();

    /**
     * Percent of chance for get critical hit
     */
    public @NonNegative int criticalHit();

    /**
     * Percent of chance for get a critical failure
     */
    public @NonNegative int criticalFailure();

    /**
     * Does the castable range is modifiable ?
     */
    public boolean modifiableRange();

    /**
     * Constraints for casting
     */
    public SpellConstraints constraints();
}
