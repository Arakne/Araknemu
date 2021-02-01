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

package fr.quatrevieux.araknemu.game.item.effect;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;

/**
 * Effect for weapon items
 */
public final class WeaponEffect implements ItemEffect {
    private final Effect effect;
    private final int min;
    private final int max;
    private final int extra;

    public WeaponEffect(Effect effect, int min, int max, int extra) {
        this.effect = effect;
        this.min = min;
        this.max = max;
        this.extra = extra;
    }

    @Override
    public Effect effect() {
        return effect;
    }

    @Override
    public ItemTemplateEffectEntry toTemplate() {
        return new ItemTemplateEffectEntry(effect, min, max, extra, "1d" + (max - min + 1) + "+" + (min - 1));
    }

    public int min() {
        return min;
    }

    public int max() {
        return max;
    }

    public int extra() {
        return extra;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (getClass() != o.getClass()) {
            return false;
        }

        final WeaponEffect that = (WeaponEffect) o;

        return min == that.min
            && max == that.max
            && extra == that.extra
            && effect == that.effect
        ;
    }

    @Override
    public int hashCode() {
        int result = effect.hashCode();

        result = 31 * result + min;
        result = 31 * result + max;
        result = 31 * result + extra;

        return result;
    }

    @Override
    public String toString() {
        return "WeaponEffect{" + effect + ":" + min + ", " + max + ", " + extra + '}';
    }
}
