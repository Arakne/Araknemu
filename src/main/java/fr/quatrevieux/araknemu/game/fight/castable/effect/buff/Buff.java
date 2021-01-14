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

package fr.quatrevieux.araknemu.game.fight.castable.effect.buff;

import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

/**
 * Persistent effect
 *
 * The duration will be taken from the effect.
 * For overload the buff effect value (or duration), you must create a new effect instance
 */
final public class Buff {
    final private SpellEffect effect;
    final private Castable action;
    final private ActiveFighter caster;
    final private PassiveFighter target;
    final private BuffHook hook;
    final private boolean canBeDispelled;

    private int remainingTurns;

    public Buff(SpellEffect effect, Castable action, ActiveFighter caster, PassiveFighter target, BuffHook hook) {
        this(effect, action, caster, target, hook, true);
    }

    public Buff(SpellEffect effect, Castable action, ActiveFighter caster, PassiveFighter target, BuffHook hook, boolean canBeDispelled) {
        this.effect = effect;
        this.action = action;
        this.caster = caster;
        this.target = target;
        this.hook = hook;

        this.remainingTurns = effect.duration();
        this.canBeDispelled = canBeDispelled;
    }

    /**
     * Get the buff effect
     */
    public SpellEffect effect() {
        return effect;
    }

    /**
     * Get the action which generates this buff
     */
    public Castable action() {
        return action;
    }

    /**
     * Get the buff caster
     */
    public ActiveFighter caster() {
        return caster;
    }

    /**
     * Get the buff target
     */
    public PassiveFighter target() {
        return target;
    }

    /**
     * Remaining turns for the buff effect
     *
     * When this value reached 0, the buff should be removed
     */
    public int remainingTurns() {
        return remainingTurns;
    }

    /**
     * Decrements the remaining turns
     *
     * You should call {@link Buff#valid()} for check if the buff is still valid or not
     */
    public void decrementRemainingTurns() {
        --remainingTurns;
    }

    /**
     * Increment remaining turns
     * Use this method when a self-buff is added
     */
    void incrementRemainingTurns() {
        ++remainingTurns;
    }

    /**
     * Get the related hook for the buff
     */
    public BuffHook hook() {
        return hook;
    }

    /**
     * Check if the buff is still valid
     */
    public boolean valid() {
        return remainingTurns > 0;
    }

    /**
     * Check if the buff can be removed
     */
    public final boolean canBeDispelled() {
        return canBeDispelled;
    }
}
