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

package fr.quatrevieux.araknemu.game.fight.castable.effect;

import fr.arakne.utils.value.Interval;
import fr.arakne.utils.value.helper.RandomUtil;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import org.checkerframework.checker.index.qual.NonNegative;

import java.util.function.BiConsumer;

/**
 * Handle effect jet value
 *
 * Computed value is :
 * ( [jet + boost] * percent / 100 + fixed + effectBonus ) * multiply
 *
 * @todo mettre des positive et non negative de partout
 */
public final class EffectValue implements Cloneable {
    enum State {
        MINIMIZED,
        RANDOMIZED,
        MAXIMIZED,
        FIXED,
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
    private @NonNegative int multiply = 1;
    private @NonNegative int value = 0;

    EffectValue(SpellEffect effect) {
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
     * Roll the dice to fix the effect value
     */
    public EffectValue roll() {
        value = jet();
        state = State.FIXED;

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
    public EffectValue multiply(@NonNegative int value) {
        this.multiply = value;

        return this;
    }

    /**
     * Get the dice value
     */
    public @NonNegative int value() {
        return applyBoost(jet());
    }

    /**
     * Get the effect value interval
     */
    public Interval interval() {
        switch (state) {
            case FIXED:
                return Interval.of(value);

            case MINIMIZED:
                return Interval.of(applyBoost(effect.min()));

            case MAXIMIZED:
                return Interval.of(applyBoost(Math.max(effect.max(), effect.min())));

            case RANDOMIZED:
            default:
                return new Interval(effect.min(), Math.max(effect.max(), effect.min())).map(this::applyBoost);
        }
    }

    @Override
    protected EffectValue clone() {
        try {
            return (EffectValue) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(); // Should not occur
        }
    }

    /**
     * Create and configure an effect value for a given caster and target
     *
     * @param effect The spell effect
     * @param caster The spell caster on which {@link fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buffs#onEffectValueCast(EffectValue)} will be called
     * @param target The target on which {@link fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buffs#onEffectValueTarget(EffectValue, PassiveFighter)} will be called
     *
     * @return The configured effect
     */
    public static EffectValue create(SpellEffect effect, PassiveFighter caster, PassiveFighter target) {
        final EffectValue value = new EffectValue(effect);

        caster.buffs().onEffectValueCast(value);
        target.buffs().onEffectValueTarget(value, caster);

        return value;
    }

    /**
     * Create and configure multiple effect values for multiple targets
     *
     * Only one "dice" will be used for all targets, but each target will receive their own EffectValue,
     * configured using {@link fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buffs#onEffectValueTarget(EffectValue, PassiveFighter)}.
     *
     * So {@link EffectValue#minimize()} and {@link EffectValue#maximize()} are effective, without change the effects value of others targets
     *
     * Usage:
     * <pre>{@code
     * public void handle(CastScope cast, CastScope.EffectScope effect) {
     *     EffectValue.forEachTargets(effect.effect(), cast.caster(), effect.targets(), (target, effectValue) -> {
     *         // Apply the effect (effectValue) on target
     *         target.life().alter(cast.caster(), effectValue.value());
     *     });
     * }
     * }</pre>
     *
     * @param effect The spell effect
     * @param caster The spell caster on which {@link fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buffs#onEffectValueCast(EffectValue)} will be called
     * @param targets Targets used to configure the effect value using {@link fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buffs#onEffectValueTarget(EffectValue, PassiveFighter)}
     * @param action Action to perform on each target, with their related effect value
     */
    public static void forEachTargets(SpellEffect effect, PassiveFighter caster, Iterable<PassiveFighter> targets, BiConsumer<PassiveFighter, EffectValue> action) {
        final EffectValue value = new EffectValue(effect);

        caster.buffs().onEffectValueCast(value);
        value.roll();

        for (PassiveFighter target : targets) {
            final EffectValue targetValue = value.clone();
            target.buffs().onEffectValueTarget(targetValue, caster);

            action.accept(target, targetValue);
        }
    }

    private @NonNegative int jet() {
        switch (state) {
            case FIXED:
                return value;

            case MINIMIZED:
                return effect.min();

            case MAXIMIZED:
                return Math.max(effect.max(), effect.min());

            case RANDOMIZED:
            default:
                return RANDOM.rand(effect.min(), effect.max());
        }
    }

    private @NonNegative int applyBoost(int value) {
        return Math.max(
            ((boost + value) * percent / 100 + fixed + effect.boost()) * multiply,
            0
        );
    }
}
