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

package fr.quatrevieux.araknemu.game.spell.effect;

import fr.quatrevieux.araknemu.game.spell.effect.area.SpellEffectArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.EffectTarget;
import org.checkerframework.checker.index.qual.NonNegative;

/**
 * Type effect for spells
 */
public interface SpellEffect {
    /**
     * Get the effect
     */
    public int effect();

    /**
     * Get the minimal jet value, or the first argument
     */
    public @NonNegative int min();

    /**
     * Get the maximal jet value, or the first argument
     *
     * If the value is zero, the effect value is constant (min)
     */
    public @NonNegative int max();

    /**
     * Get the boost value
     * The boost value will be added to final effect result (fixed damage / heal)
     */
    public default int boost() {
        return 0;
    }

    /**
     * Get the special effect value
     * Used by invocation and boost spells
     *
     * Unlike min and max, this value can be negative
     */
    public int special();

    /**
     * Get the effect duration
     * If this value is zero, the effect will be applied immediately
     */
    public @NonNegative int duration();

    /**
     * The effect probability in percent
     * If this value is zero, the effect will always be applied
     * For not null probability, only one effect will be choose across all "conditional" effects
     */
    public @NonNegative int probability();

    /**
     * Extra effect text. Used for dice notation
     */
    public String text();

    /**
     * Get the effect area
     */
    public SpellEffectArea area();

    /**
     * Get the effect target
     */
    public EffectTarget target();
}
