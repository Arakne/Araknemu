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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable.effect;

import fr.arakne.utils.value.helper.RandomUtil;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

/**
 * Handle effect jet value
 *
 * Computed value is :
 * ( [jet + boost] * percent / 100 + fixed + effectBonus ) * multiply
 */
public final class EffectValue {
    enum State {
        MINIMIZED,
        RANDOMIZED,
        MAXIMIZED,
        MEAN
    }

    /**
     * EffectValue is a short life object, and random is only used 1 time
     */
    private static final RandomUtil RANDOM = RandomUtil.createShared();

    private final SpellEffect effect;

    private State state = State.RANDOMIZED;
    private int boost = 0;
    private int percent = 100;
    private int fixed = 0;
    private int multiply = 1;

    public EffectValue(SpellEffect effect) {
        this.effect = effect;
    }

    /**
     * Maximize the value
     */
    public EffectValue maximize() {
        state = State.MAXIMIZED;

        return this;
    }

    /**
     * Minimize the value
     */
    public EffectValue minimize() {
        state = State.MINIMIZED;

        return this;
    }

    /**
     * The value will be a random value between [min, max]
     */
    public EffectValue randomize() {
        state = State.RANDOMIZED;

        return this;
    }

    /**
     * Compute the mean value (i.e. (min + max) / 2)
     */
    public EffectValue mean() {
        state = State.MEAN;

        return this;
    }

    /**
     * Boost the dice value
     * The boost will be added at dice value
     * So the boosted value will be increased with percent
     *
     * Ex: [5, 10] + boost 5 + 50% => [15, 22]
     *
     * @param value The boosted value
     */
    public EffectValue boost(int value) {
        this.boost = value;

        return this;
    }

    /**
     * Boost with percent value
     *
     * Ex: [5, 10] + 50% => [7, 15]
     */
    public EffectValue percent(int value) {
        this.percent += value;

        return this;
    }

    /**
     * Add fixed value
     * The fixed value will be added after percent value
     *
     * Ex: [5, 10] + 5 fixed => [10, 15]
     */
    public EffectValue fixed(int value) {
        this.fixed += value;

        return this;
    }

    /**
     * Multiply the result
     * Unlike percent, the multiplier will be used at the end of the operation.
     * So, it multiplies jet, percent and fixed bonus
     */
    public EffectValue multiply(int value) {
        this.multiply = value;

        return this;
    }

    /**
     * Get the dice value
     */
    public int value() {
        final int value = ((boost + jet()) * percent / 100 + fixed + effect.boost()) * multiply;

        return Math.max(value, 0);
    }

    private int jet() {
        switch (state) {
            case MINIMIZED:
                return effect.min();

            case MAXIMIZED:
                return effect.max() < effect.min() ? effect.min() : effect.max();

            case MEAN:
                return effect.max() < effect.min()
                    ? effect.min()
                    : (effect.min() + effect.max()) / 2
                ;

            case RANDOMIZED:
            default:
                return RANDOM.rand(effect.min(), effect.max());
        }
    }
}
