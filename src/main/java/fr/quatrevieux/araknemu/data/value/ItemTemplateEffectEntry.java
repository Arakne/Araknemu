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

import fr.quatrevieux.araknemu.data.constant.Effect;

/**
 * Item effect entry
 */
public final class ItemTemplateEffectEntry {
    private final Effect effect;
    private final int min;
    private final int max;
    private final int special;
    private final String text;

    public ItemTemplateEffectEntry(Effect effect, int min, int max, int special, String text) {
        this.effect = effect;
        this.min = min;
        this.max = max;
        this.special = special;
        this.text = text;
    }

    /**
     * Get the effect
     */
    public Effect effect() {
        return effect;
    }

    /**
     * Get the minimum value
     */
    public int min() {
        return min;
    }

    /**
     * Get the max value.
     * If zero, the effect will be a constant
     */
    public int max() {
        return max;
    }

    /**
     * Get special value (not range value, reference to an external entity like spell)
     */
    public int special() {
        return special;
    }

    /**
     * Get text value
     * For basic effect, this value will be the dice (ex: 1d5+12) value
     */
    public String text() {
        return text;
    }
}
