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
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import org.checkerframework.checker.index.qual.GTENegativeOne;

/**
 * Persistent effect
 *
 * The duration will be taken from the effect.
 * For overload the buff effect value (or duration), you must create a new effect instance
 */
public final class Buff {
    private final SpellEffect effect;
    private final Castable action;
    private final Fighter caster;
    private final Fighter target;
    private final BuffHook hook;
    private final boolean canBeDispelled;

    private @GTENegativeOne int remainingTurns;

    public Buff(SpellEffect effect, Castable action, Fighter caster, Fighter target, BuffHook hook) {
        this(effect, action, caster, target, hook, true);
    }

    public Buff(SpellEffect effect, Castable action, Fighter caster, Fighter target, BuffHook hook, boolean canBeDispelled) {
        this.effect = effect;
        this.action = action;
        this.caster = caster;
        this.target = target;
        this.hook = hook;
        this.canBeDispelled = canBeDispelled;

        this.remainingTurns = effect.duration();
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
    public Fighter caster() {
        return caster;
    }

    /**
     * Get the buff target
     */
    public Fighter target() {
        return target;
    }

    /**
     * Remaining turns for the buff effect
     *
     * When this value reached 0, the buff should be removed
     * In case of infinite effect, the returned value is -1
     */
    public @GTENegativeOne int remainingTurns() {
        return remainingTurns;
    }

    /**
     * Decrements the remaining turns
     *
     * You should call {@link Buff#valid()} for check if the buff is still valid or not
     */
    public void decrementRemainingTurns() {
        if (remainingTurns > 0) {
            --remainingTurns;
        }
    }

    /**
     * Increment remaining turns
     * Use this method when a self-buff is added
     */
    void incrementRemainingTurns() {
        if (remainingTurns != -1) {
            ++remainingTurns;
        }
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
        return remainingTurns != 0;
    }

    /**
     * Check if the buff can be removed
     */
    public boolean canBeDispelled() {
        return canBeDispelled;
    }
}
