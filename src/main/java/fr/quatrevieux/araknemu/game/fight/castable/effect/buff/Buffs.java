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
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.Damage;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.ReflectedDamage;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;

/**
 * Handle and store buff list for a fighter
 */
public interface Buffs extends Iterable<Buff> {
    /**
     * Add and start a buff
     */
    public void add(Buff buff);

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
    public boolean onCastTarget(CastScope cast);

    /**
     * @see BuffHook#onDirectDamage(Buff, ActiveFighter, Damage)
     */
    public void onDirectDamage(ActiveFighter caster, Damage value);

    /**
     * @see BuffHook#onBuffDamage(Buff, Buff, Damage)
     */
    public void onBuffDamage(Buff poison, Damage value);

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
     * @see BuffHook#onEndTurn(Buff)
     */
    public void onEndTurn();

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
    public boolean removeByCaster(PassiveFighter caster);
}
