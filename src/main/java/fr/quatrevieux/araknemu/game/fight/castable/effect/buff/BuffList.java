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
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.Damage;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.ReflectedDamage;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;
import fr.quatrevieux.araknemu.network.game.out.fight.AddBuff;
import fr.quatrevieux.araknemu.util.SafeLinkedList;
import org.checkerframework.checker.index.qual.Positive;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Handle buff list for a fighter
 */
public final class BuffList implements Iterable<Buff>, Buffs {
    private final Fighter fighter;
    private final SafeLinkedList<Buff> buffs = new SafeLinkedList<>();

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

    @Override
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

    @Override
    public boolean onStartTurn() {
        boolean result = true;

        for (Buff buff : buffs) {
            result &= buff.hook().onStartTurn(buff);
        }

        return result;
    }

    @Override
    public void onEndTurn(Turn turn) {
        for (Buff buff : buffs) {
            buff.hook().onEndTurn(buff, turn);
        }
    }

    @Override
    public boolean onCastTarget(CastScope cast) {
        for (Buff buff : buffs) {
            if (!buff.hook().onCastTarget(buff, cast)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void onDirectDamage(ActiveFighter caster, Damage value) {
        for (Buff buff : buffs) {
            buff.hook().onDirectDamage(buff, caster, value);
        }
    }

    @Override
    public void onIndirectDamage(ActiveFighter caster, Damage value) {
        for (Buff buff : buffs) {
            buff.hook().onIndirectDamage(buff, caster, value);
        }
    }

    @Override
    public void onBuffDamage(Buff poison, Damage value) {
        for (Buff buff : buffs) {
            buff.hook().onBuffDamage(buff, poison, value);
        }
    }

    @Override
    public void onDirectDamageApplied(ActiveFighter caster, @Positive int value) {
        for (Buff buff : buffs) {
            buff.hook().onDirectDamageApplied(buff, caster, value);
        }
    }

    @Override
    public void onLifeAltered(int value) {
        for (Buff buff : buffs) {
            buff.hook().onLifeAltered(buff, value);
        }
    }

    @Override
    public void onReflectedDamage(ReflectedDamage damage) {
        for (Buff buff : buffs) {
            buff.hook().onReflectedDamage(buff, damage);
        }
    }

    @Override
    public void onCastDamage(Damage damage, PassiveFighter target) {
        for (Buff buff : buffs) {
            buff.hook().onCastDamage(buff, damage, target);
        }
    }

    @Override
    public void onEffectValueCast(EffectValue value) {
        for (Buff buff : buffs) {
            buff.hook().onEffectValueCast(buff, value);
        }
    }

    @Override
    public void onEffectValueTarget(EffectValue value, PassiveFighter caster) {
        for (Buff buff : buffs) {
            buff.hook().onEffectValueTarget(buff, value, caster);
        }
    }

    @Override
    public void refresh() {
        removeIf(buff -> {
            buff.decrementRemainingTurns();

            return !buff.valid();
        });
    }

    @Override
    public boolean removeAll() {
        return removeIf(Buff::canBeDispelled);
    }

    @Override
    public boolean removeByCaster(PassiveFighter caster) {
        return removeIf(buff -> buff.caster().equals(caster));
    }

    /**
     * Remove buff by a predicate
     *
     * @param predicate Takes the buff as parameter, and return true to delete (and terminate) the buff
     *
     * @return true if there is a change (i.e. a buff is terminated)
     */
    private boolean removeIf(Predicate<Buff> predicate) {
        final Iterator<Buff> iterator = buffs.iterator();

        boolean hasChanged = false;

        while (iterator.hasNext()) {
            final Buff buff = iterator.next();

            if (predicate.test(buff)) {
                iterator.remove();
                buff.hook().onBuffTerminated(buff);
                hasChanged = true;
            }
        }

        return hasChanged;
    }
}
