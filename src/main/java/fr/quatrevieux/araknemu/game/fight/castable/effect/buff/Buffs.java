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

package fr.quatrevieux.araknemu.game.fight.castable.effect.buff;

import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.Damage;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.ReflectedDamage;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;
import org.checkerframework.checker.index.qual.Positive;

/**
 * Handle and store buff list for a fighter
 */
public interface Buffs extends Iterable<Buff> {
    /**
     * Add and start a buff
     */
    public void add(Buff buff);

    /**
     * @see BuffHook#onCast(Buff, CastScope)
     */
    public void onCast(CastScope<Fighter> cast);

    /**
     * Apply buffs when the fighter is a target of a cast
     *
     * If false is returned, the scope targets should be reloaded to call this hook on new targets,
     * and following hooks will be ignored.
     *
     * @return true to continue, or false if the target has changed (or is removed)
     *
     * @see BuffHook#onCastTarget(Buff, CastScope)
     */
    public boolean onCastTarget(CastScope<Fighter> cast);

    /**
     * @see BuffHook#onDirectDamage(Buff, Fighter, Damage)
     */
    public void onDirectDamage(Fighter caster, Damage value);

    /**
     * @see BuffHook#onIndirectDamage(Buff, Fighter, Damage)
     */
    public void onIndirectDamage(Fighter caster, Damage value);

    /**
     * @see BuffHook#onBuffDamage(Buff, Buff, Damage)
     */
    public void onBuffDamage(Buff poison, Damage value);

    /**
     * @see BuffHook#onDirectDamageApplied(Buff, Fighter, int)
     */
    public void onDirectDamageApplied(Fighter caster, @Positive int value);

    /**
     * @see BuffHook#onLifeAltered(Buff, int)
     */
    public void onLifeAltered(int value);

    /**
     * @see BuffHook#onReflectedDamage(Buff, ReflectedDamage)
     */
    public void onReflectedDamage(ReflectedDamage damage);

    /**
     * @see BuffHook#onStartTurn(Buff)
     */
    public boolean onStartTurn();

    /**
     * @see BuffHook#onEndTurn(Buff, Turn)
     */
    public void onEndTurn(Turn turn);

    /**
     * @see BuffHook#onCastDamage(Buff, Damage, Fighter)
     */
    public void onCastDamage(Damage damage, Fighter target);

    /**
     * @see BuffHook#onEffectValueCast(Buff, EffectValue)
     */
    public void onEffectValueCast(EffectValue value);

    /**
     * @see BuffHook#onEffectValueTarget(Buff, EffectValue)
     */
    public void onEffectValueTarget(EffectValue value);

    /**
     * Refresh the buff list after turn end
     */
    public void refresh();

    /**
     * Remove all buffs than can be removed, and fire {@link BuffHook#onBuffTerminated(Buff)}
     *
     * @return true if there is at least one removed buff
     */
    public boolean removeAll();

    /**
     * Remove all buffs casted by the given fighter
     *
     * @return true if there is at least one removed buff
     */
    public boolean removeByCaster(Fighter caster);
}
