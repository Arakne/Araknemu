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

import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.Damage;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.network.game.out.fight.AddBuff;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Handle buff list for a fighter
 */
final public class BuffList implements Iterable<Buff> {
    final private Fighter fighter;
    final private Collection<Buff> buffs = new LinkedList<>();

    public BuffList(Fighter fighter) {
        this.fighter = fighter;
    }

    @Override
    public Iterator<Buff> iterator() {
        return buffs.iterator();
    }

    /**
     * Get the Buff stream
     */
    public Stream<Buff> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * Add and start a buff
     */
    public void add(Buff buff) {
        buffs.add(buff);
        buff.hook().onBuffStarted(buff);

        if (buff.remainingTurns() == 0) {
            buff.incrementRemainingTurns();
        }

        fighter.fight().send(new AddBuff(buff));

        // Add one turn on self-buff
        if (fighter.equals(buff.caster())) {
            buff.incrementRemainingTurns();
        }
    }

    /**
     * @see BuffHook#onStartTurn(Buff)
     */
    public boolean onStartTurn() {
        boolean result = true;

        for (Buff buff : buffs) {
            result &= buff.hook().onStartTurn(buff);
        }

        return result;
    }

    /**
     * @see BuffHook#onEndTurn(Buff)
     */
    public void onEndTurn() {
        for (Buff buff : buffs) {
            buff.hook().onEndTurn(buff);
        }
    }

    /**
     * @see BuffHook#onCastTarget(Buff, CastScope);
     */
    public void onCastTarget(CastScope cast) {
        for (Buff buff : buffs) {
            buff.hook().onCastTarget(buff, cast);
        }
    }

    /**
     * @see BuffHook#onDamage(Buff, Damage);
     */
    public void onDamage(Damage value) {
        for (Buff buff : buffs) {
            buff.hook().onDamage(buff, value);
        }
    }

    /**
     * Refresh the buff list after turn end
     */
    public void refresh() {
        Iterator<Buff> iterator = buffs.iterator();

        while (iterator.hasNext()) {
            Buff buff = iterator.next();
            buff.decrementRemainingTurns();

            if (!buff.valid()) {
                iterator.remove();
                buff.hook().onBuffTerminated(buff);
            }
        }
    }
}
