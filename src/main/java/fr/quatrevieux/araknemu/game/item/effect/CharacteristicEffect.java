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

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;

/**
 * Item effect for characteristics
 */
final public class CharacteristicEffect implements ItemEffect {
    final private Effect effect;
    final private int value;
    final private int multiplier;
    final private Characteristic characteristic;

    public CharacteristicEffect(Effect effect, int value, int multiplier, Characteristic characteristic) {
        this.effect = effect;
        this.value = value;
        this.multiplier = multiplier;
        this.characteristic = characteristic;
    }

    @Override
    public Effect effect() {
        return effect;
    }

    @Override
    public ItemTemplateEffectEntry toTemplate() {
        return new ItemTemplateEffectEntry(effect, value, 0, 0, "0d0+" + value);
    }

    /**
     * Get the effect value. This value is always positive
     *
     * @see CharacteristicEffect#boost() for the real characteristic value
     */
    public int value() {
        return value;
    }

    /**
     * Get the modified characteristic
     */
    public Characteristic characteristic() {
        return characteristic;
    }

    /**
     * Get the modifier value. This value can be negative
     *
     * @see CharacteristicEffect#value() for the effect value
     */
    public int boost() {
        return multiplier * value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (getClass() != o.getClass()) {
            return false;
        }

        final CharacteristicEffect that = (CharacteristicEffect) o;

        return value == that.value
            && effect == that.effect
        ;
    }

    @Override
    public int hashCode() {
        int result = effect.hashCode();

        result = 31 * result + value;

        return result;
    }

    @Override
    public String toString() {
        return "CharacteristicEffect{" + effect + ":" + value + "}";
    }
}
