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

package fr.quatrevieux.araknemu.data.value;

import org.checkerframework.checker.index.qual.GTENegativeOne;
import org.checkerframework.checker.index.qual.NonNegative;

/**
 * Effect for {@link fr.quatrevieux.araknemu.data.world.entity.SpellTemplate}
 */
public final class SpellTemplateEffect {
    private final int effect;
    private final @NonNegative int min;
    private final @NonNegative int max;
    private final int special;
    private final @GTENegativeOne int duration;
    private final @NonNegative int probability;
    private final String text;

    public SpellTemplateEffect(int effect, @NonNegative int min, @NonNegative int max, int special, @GTENegativeOne int duration, @NonNegative int probability, String text) {
        this.effect = effect;
        this.min = min;
        this.max = max;
        this.special = special;
        this.duration = duration;
        this.probability = probability;
        this.text = text;
    }

    /**
     * Get the effect
     */
    public int effect() {
        return effect;
    }

    /**
     * Get the minimal jet value, or the first argument
     */
    public @NonNegative int min() {
        return min;
    }

    /**
     * Get the maximal jet value, or the first argument
     *
     * If the value is zero, the effect value is constant (min)
     */
    public @NonNegative int max() {
        return max;
    }

    /**
     * Get the special effect value
     * Used by invocation and boost spells
     *
     * Unlike min and max, this value can be negative
     */
    public int special() {
        return special;
    }

    /**
     * Get the effect duration
     * If this value is zero, the effect will be applied immediately
     * If the value is -1, the duration will be considered as infinite
     */
    public @GTENegativeOne int duration() {
        return duration;
    }

    /**
     * The effect probability in percent
     * If this value is zero, the effect will always be applied
     * For not null probability, only one effect will be choose across all "conditional" effects
     */
    public @NonNegative int probability() {
        return probability;
    }

    /**
     * Extra effect text. Used for dice notation
     */
    public String text() {
        return text;
    }
}
