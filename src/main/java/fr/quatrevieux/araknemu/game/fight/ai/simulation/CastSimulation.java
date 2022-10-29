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

package fr.quatrevieux.araknemu.game.fight.ai.simulation;

import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.spell.Spell;
import org.checkerframework.checker.index.qual.Positive;

/**
 * The simulation result of a cast
 */
public final class CastSimulation {
    /**
     * The rate to apply on a poison damage value
     */
    public static final double POISON_RATE = 0.75;

    private final Spell spell;
    private final FighterData caster;
    private final BattlefieldCell target;

    private double enemiesLife;
    private double alliesLife;
    private double selfLife;

    private double enemiesBoost;
    private double alliesBoost;
    private double selfBoost;

    private double killedAllies;
    private double killedEnemies;
    private double suicide;

    private double actionPointsModifier = 0;

    public CastSimulation(Spell spell, FighterData caster, BattlefieldCell target) {
        this.spell = spell;
        this.caster = caster;
        this.target = target;
    }

    /**
     * The enemies life diff (negative value for damage, positive for heal)
     */
    public double enemiesLife() {
        return enemiesLife;
    }

    /**
     * The allies (without self) life diff (negative value for damage, positive for heal)
     */
    public double alliesLife() {
        return alliesLife;
    }

    /**
     * The self (caster) life diff (negative value for damage, positive for heal)
     */
    public double selfLife() {
        return selfLife;
    }

    /**
     * Number of killed allies
     */
    public double killedAllies() {
        return killedAllies;
    }

    /**
     * Number of killed enemies
     */
    public double killedEnemies() {
        return killedEnemies;
    }

    /**
     * The suicide (self kill) probability
     *
     * @return The probability between 0 and 1
     */
    public double suicideProbability() {
        return Math.min(suicide, 1);
    }

    /**
     * Heal a target
     *
     * @param value The heal value
     * @param target The target fighter
     */
    public void addHeal(final Interval value, final FighterData target) {
        final int targetLostLife = target.life().max() - target.life().current();

        apply(new EffectValueComputer() {
            @Override
            public double lifeChange() {
                return computeCappedEffect(value, targetLostLife);
            }
        }, target);
    }

    /**
     * Heal a target using a buff
     *
     * @param value The heal value
     * @param target The target fighter
     */
    public void addHealBuff(final Interval value, final @Positive int duration, final FighterData target) {
        addHeal(value, target);

        if (duration > 1) {
            addBoost(value.average() * POISON_RATE * (duration - 1), target);
        }
    }

    /**
     * Add a damage on the target
     *
     * @param value The damage value
     * @param target The target fighter
     */
    public void addDamage(final Interval value, final FighterData target) {
        final int targetLife = target.life().current();
        final double killProbability = cappedProbability(value, targetLife);

        apply(new EffectValueComputer() {
            @Override
            public double killProbability() {
                return killProbability;
            }

            @Override
            public double lifeChange() {
                return -computeCappedEffect(value, targetLife, killProbability);
            }
        }, target);
    }

    /**
     * Add a poison (damage on multiple turns) on the target
     *
     * @param value The damage value. Should be positive
     * @param duration The poison duration in turns
     * @param target The target fighter
     */
    public void addPoison(final Interval value, final @Positive int duration, final FighterData target) {
        apply(new EffectValueComputer() {
            @Override
            public double lifeChange() {
                return -computeCappedEffect(
                    value.map(v -> v * duration),
                    target.life().current()
                ) * POISON_RATE;
            }
        }, target);
    }

    /**
     * Action point alternation for the current fighter
     * A positive value means that the current spell will add action points on the current turn of the fighter
     *
     * This value will be removed from spell action point cost for compute actual action point cost.
     */
    public void alterActionPoints(double value) {
        actionPointsModifier += value;
    }

    /**
     * Apply the effect values to a target
     *
     * @param values Computed effect values
     * @param target The target
     */
    public void apply(EffectValueComputer values, FighterData target) {
        if (target.equals(caster)) {
            selfLife += values.lifeChange();
            suicide += values.killProbability();
            selfBoost += values.boost();
        } else if (target.team().equals(caster.team())) {
            alliesLife += values.lifeChange();
            killedAllies += values.killProbability();
            alliesBoost += values.boost();
        } else {
            enemiesLife += values.lifeChange();
            killedEnemies += values.killProbability();
            enemiesBoost += values.boost();
        }
    }

    /**
     * The enemy boost value.
     * Negative value for malus, and positive for bonus
     */
    public double enemiesBoost() {
        return enemiesBoost;
    }

    /**
     * The allies boost value (without self).
     * Negative value for malus, and positive for bonus
     */
    public double alliesBoost() {
        return alliesBoost;
    }

    /**
     * The self boost value.
     * Negative value for malus, and positive for bonus
     */
    public double selfBoost() {
        return selfBoost;
    }

    /**
     * Add a boost to the target
     *
     * @param value The boost value. Can be negative for a malus
     * @param target The target fighter
     */
    public void addBoost(double value, FighterData target) {
        apply(new EffectValueComputer() {
            @Override
            public double boost() {
                return value;
            }
        }, target);
    }

    /**
     * Get the simulated spell caster
     */
    public FighterData caster() {
        return caster;
    }

    /**
     * Get the simulated spell
     */
    public Spell spell() {
        return spell;
    }

    /**
     * Get the target cell
     */
    public BattlefieldCell target() {
        return target;
    }

    /**
     * Get the actual action points cost of the current action
     * Actions points change on the current fighter will be taken in account
     *
     * ex: if the spell cost 4 AP, but give 1 AP, the cost will be 3 AP
     *
     * The minimal value is bounded to 0.1
     */
    public double actionPointsCost() {
        return Math.max(spell.apCost() - actionPointsModifier, 0.1);
    }

    /**
     * Merge the simulation result into the current simulation
     *
     * All results will be added considering the percent,
     * which represents the probability of the simulation
     *
     * @param simulation The simulation to merge
     * @param percent The simulation chance int percent. This value as interval of [0, 100]
     */
    public void merge(CastSimulation simulation, double percent) {
        enemiesLife += simulation.enemiesLife * percent / 100d;
        alliesLife += simulation.alliesLife * percent / 100d;
        selfLife += simulation.selfLife * percent / 100d;

        enemiesBoost += simulation.enemiesBoost * percent / 100d;
        alliesBoost += simulation.alliesBoost * percent / 100d;
        selfBoost += simulation.selfBoost * percent / 100d;

        killedAllies += simulation.killedAllies * percent / 100d;
        killedEnemies += simulation.killedEnemies * percent / 100d;
        suicide += simulation.suicide * percent / 100d;

        actionPointsModifier += simulation.actionPointsModifier * percent / 100d;
    }

    /**
     * Compute the chance to rise max value of an effect
     *
     * Ex:
     * - Enemy has 50 life points
     * - The spell can inflict 25 to 75 damage
     * - So the spell has 50% chance of kill the enemy (25 -> 49: enemy is alive, 50 -> 75 enemy is dead)
     *
     * @param value The effect value interval
     * @param maxValue The maximum allowed value (capped value)
     *
     * @return The probability to rise the max value of the effect. 0 if max less than maxValue, 1 if min higher than maxValue, any value between 0 and 1 in other cases
     */
    private double cappedProbability(Interval value, double maxValue) {
        if (value.min() >= maxValue) {
            return 1;
        }

        if (value.max() < maxValue) {
            return 0;
        }

        return (value.max() - maxValue) / value.amplitude();
    }

    /**
     * Compute value of a capped effect
     *
     * Ex:
     * - Enemy has 50 life points
     * - The spell can inflict 25 to 75 damage
     * - If spell damage is higher than 50 (50 -> 75, 50% of chance), it will be capped to 50
     * - If spell damage is less than 50 (25 -> 49, 50% of chance), any value in the interval can happen, so average value is (25 + 49) / 2 ~= 37
     * - So the real average damage is : 50% * 50 + 50% * 37 = 43.5
     *
     * @param value The effect value interval
     * @param maxValue The maximum allowed value (capped value)
     * @param maxProbability The probability to rise the max value. Use {@link CastSimulation#cappedProbability(Interval, double)} to compute this value
     *
     * @return The real effect value.
     *     - If min is higher than maxValue return the maxValue
     *     - If max is less than maxValue return the average value of the interval
     *     - Else, takes the capped probability in account to compute the value
     */
    private double computeCappedEffect(Interval value, double maxValue, double maxProbability) {
        if (maxProbability == 1) {
            return maxValue;
        }

        if (maxProbability == 0) {
            return value.average();
        }

        final double cappedAvgValue = ((double) value.min() + maxValue) / 2d;

        return cappedAvgValue * (1d - maxProbability) + maxValue * maxProbability;
    }

    /**
     * Compute value of a capped effect
     *
     * Ex:
     * - Enemy has 50 life points
     * - The spell can inflict 25 to 75 damage
     * - If spell damage is higher than 50 (50 -> 75, 50% of chance), it will be capped to 50
     * - If spell damage is less than 50 (25 -> 49, 50% of chance), any value in the interval can happen, so average value is (25 + 49) / 2 ~= 37
     * - So the real average damage is : 50% * 50 + 50% * 37 = 43.5
     *
     * @param value The effect value interval
     * @param maxValue The maximum allowed value (capped value)
     *
     * @return The real effect value.
     *     - If min is higher than maxValue return the maxValue
     *     - If max is less than maxValue return the average value of the interval
     *     - Else, takes the capped probability in account to compute the value
     */
    private double computeCappedEffect(Interval value, double maxValue) {
        return computeCappedEffect(value, maxValue, cappedProbability(value, maxValue));
    }

    /**
     * Structure for compute applied effects values
     */
    public interface EffectValueComputer {
        /**
         * The kill probability
         *
         * @return a double value between 0 and 1
         */
        public default double killProbability() {
            return 0;
        }

        /**
         * The changed life of the target
         * Return a negative value for damage, or a positive for heal
         * Do nothing is return 0
         *
         * Note: the computed value must take in account the target current life
         */
        public default double lifeChange() {
            return 0;
        }

        /**
         * The boost value of the effect
         * Negative value for debuff, and positive for buff
         */
        public default double boost() {
            return 0;
        }
    }
}
