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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import org.checkerframework.checker.index.qual.NonNegative;

/**
 * Compute reflected damage of a cast
 *
 * The reflected damage is lowered by the target resistance, and cannot be higher than the half
 * of the original cast damage.
 *
 * See: https://dofuswiki.fandom.com/wiki/Damage#Defensive
 */
public final class ReflectedDamage implements MultipliableDamage {
    private final Damage castDamage;
    private Fighter target;
    private int multiplier = 1;

    public ReflectedDamage(Damage castDamage, Fighter target) {
        this.castDamage = castDamage;
        this.target = target;
    }

    /**
     * Fighter which will take reflected damage
     */
    public Fighter target() {
        return target;
    }

    /**
     * Base reflected damage
     * This value is the effectively reflected value, not the applied one.
     * A buff can change the real applied damage by using {@link ReflectedDamage#multiply(int)}
     */
    public @NonNegative int baseValue() {
        final int baseCastDamage = castDamage.value();

        if (baseCastDamage <= 0) {
            return 0;
        }

        final Characteristics characteristics = target.characteristics();
        final int percentResistance = characteristics.get(castDamage.element().percentResistance());
        final int fixedResistance = characteristics.get(castDamage.element().fixedResistance());

        // Apply target resistances
        final int base = (castDamage.reflectedDamage() * (100 - percentResistance)) / 100 - fixedResistance;

        if (base < 0) {
            return 0;
        }

        // Returned damage can be at most half of hit damage
        return Math.min(base, baseCastDamage / 2);
    }

    @Override
    public int value() {
        return multiplier * baseValue();
    }

    /**
     * Change the reflection target
     */
    public void changeTarget(Fighter newTarget) {
        this.target = newTarget;
    }

    @Override
    public ReflectedDamage multiply(int factor) {
        this.multiplier = factor;

        return this;
    }
}
