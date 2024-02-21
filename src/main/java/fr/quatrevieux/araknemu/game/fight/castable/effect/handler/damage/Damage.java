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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage;

import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectsUtils;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import org.checkerframework.checker.index.qual.NonNegative;

/**
 * Compute suffered damage
 *
 * Formula :
 * ((value * percent / 100) * (0.9 ^ distance) - fixed - reduce) * multiply
 */
public final class Damage implements MultipliableDamage {
    private final @NonNegative int value;
    private final Element element;

    private int multiply = 1;
    private int fixed = 0;
    private @NonNegative int percent = 100;
    private @NonNegative int reduce = 0;
    private @NonNegative int returned = 0;
    private @NonNegative int distance = 0;

    public Damage(@NonNegative int value, Element element) {
        this.value = value;
        this.element = element;
    }

    /**
     * Get the damage element
     */
    public Element element() {
        return element;
    }

    @Override
    public int value() {
        final int reducedDamage = (baseDamage() * percent / 100) - fixed - reduce;

        if (reducedDamage <= 0) {
            return 0;
        }

        return reducedDamage * multiply;
    }

    /**
     * Get the base damage, before applying any reduction or multiplication
     * Only the distance attenuation is applied
     */
    public @NonNegative int baseDamage() {
        final int value = this.value;

        if (value == 0) {
            return 0;
        }

        return EffectsUtils.applyDistanceAttenuation(value, distance);
    }

    /**
     * Reduce damage in percent
     */
    public Damage percent(int percent) {
        this.percent = Math.max(this.percent - percent, 0);

        return this;
    }

    /**
     * Reduce fixed damage
     */
    public Damage fixed(int fixed) {
        this.fixed += fixed;

        return this;
    }

    @Override
    public Damage multiply(int factor) {
        this.multiply *= factor;

        return this;
    }

    /**
     * Reduce fixed damage with buff effect
     */
    public Damage reduce(@NonNegative int value) {
        this.reduce += value;

        return this;
    }

    /**
     * Add reflected damage
     */
    public Damage reflect(@NonNegative int value) {
        this.returned += value;

        return this;
    }

    /**
     * Set the distance between the center of the effect and target
     *
     * Each distance reduce damage by 10% (cumulated), so distance 1 reduce damage by 10%, distance 2 by 19%, etc...
     */
    public Damage distance(@NonNegative int distance) {
        this.distance = distance;

        return this;
    }

    /**
     * Get the damage reduction value from armor buff effects
     */
    public @NonNegative int reducedDamage() {
        return reduce;
    }

    /**
     * How much damage has been reflected by the target ?
     */
    public @NonNegative int reflectedDamage() {
        return returned;
    }
}
