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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.simulation;

import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;

/**
 * Represent theoretical score of a spell
 *
 * Unlike {@link CastSimulation}, this class does not represent an actual cast
 * but a simplified version of spell average effects
 */
public final class SpellScore {
    private @NonNegative int attackRange;
    private @NonNegative int damage;
    private @NonNegative int heal;
    private @NonNegative int boost;
    private @NonNegative int debuff;
    private boolean suicide = false;

    public SpellScore() {
        this(0);
    }

    /**
     * @param attackRange The spell maximum range
     */
    public SpellScore(@NonNegative int attackRange) {
        this.attackRange = attackRange;
    }

    /**
     * Add theoretical spell damage
     */
    public void addDamage(@NonNegative int damage) {
        this.damage += damage;
    }

    /**
     * Add theoretical heal
     */
    public void addHeal(@NonNegative int heal) {
        this.heal += heal;
    }

    /**
     * Add boost score
     */
    public void addBoost(@NonNegative int boost) {
        this.boost += boost;
    }

    /**
     * Add malus score
     */
    public void addDebuff(@NonNegative int debuff) {
        this.debuff += debuff;
    }

    /**
     * Define if the spell will kill the caster
     *
     * Note: an OR operator will be used, so if "suicide" has already been set before, it will be kept
     */
    public void setSuicide(boolean suicide) {
        this.suicide |= suicide;
    }

    /**
     * Multiply all computed scores by the given value
     *
     * @return this instance
     */
    public SpellScore multiply(@Positive int multiplier) {
        heal *= multiplier;
        damage *= multiplier;
        boost *= multiplier;
        debuff *= multiplier;

        return this;
    }

    /**
     * Merge another spell score to the current one
     * The current instance will be modified, and only best score of both are kept
     *
     * @param other Other score to merge. Will be unmodified.
     */
    public void merge(SpellScore other) {
        if (other.damage > damage) {
            damage = other.damage;
        }

        if (other.heal > heal) {
            heal = other.heal;
        }

        if (other.boost > boost) {
            boost = other.boost;
        }

        if (other.debuff > debuff) {
            debuff = other.debuff;
        }

        if (other.attackRange > attackRange && other.isAttack()) {
            attackRange = other.attackRange;
        }

        suicide |= other.suicide;
    }

    /**
     * Get the total theoretical damage of the spell
     */
    public @NonNegative int damage() {
        return damage;
    }

    /**
     * Get the total theoretical heal of the spell
     */
    public @NonNegative int heal() {
        return heal;
    }

    /**
     * Get the total theoretical boost score of the spell
     */
    public @NonNegative int boost() {
        return boost;
    }

    /**
     * Get the total theoretical malus score of the spell
     */
    public @NonNegative int debuff() {
        return debuff;
    }

    /**
     * Get the sum of all scores
     */
    public @NonNegative int score() {
        return damage + heal + boost + debuff;
    }

    /**
     * The maximum attack range of the spell
     * Only attack effects are considered by this range
     */
    public @NonNegative int attackRange() {
        return attackRange;
    }

    /**
     * Does the spell (or creature) will produce more damage than heal ?
     */
    public boolean isAttack() {
        return damage > heal;
    }

    /**
     * Does the spell (or creature) will produce more heal than damage ?
     */
    public boolean isHeal() {
        return heal > damage;
    }

    /**
     * Does the spell will kill the caster ?
     */
    public boolean isSuicide() {
        return suicide;
    }
}
