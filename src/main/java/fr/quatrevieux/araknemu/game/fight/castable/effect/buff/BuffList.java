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

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.Damage;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.ReflectedDamage;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;
import fr.quatrevieux.araknemu.network.game.out.fight.AddBuff;
import fr.quatrevieux.araknemu.util.SafeLinkedList;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;

import java.util.Iterator;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Handle buff list for a fighter
 */
public final class BuffList implements Iterable<FightBuff>, Buffs<FightBuff>, BuffListHooks {
    private final Fighter fighter;
    private final SafeLinkedList<FightBuff> buffs = new SafeLinkedList<>();

    public BuffList(Fighter fighter) {
        this.fighter = fighter;
    }

    @Override
    public Iterator<FightBuff> iterator() {
        return buffs.iterator();
    }

    /**
     * Get the Buff stream
     */
    public Stream<FightBuff> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    @Override
    public void add(FightBuff buff) {
        final boolean isPlayingFighter = fighter.isPlaying();

        buffs.add(buff);
        buff.hook().onBuffStarted(buff);

        if (buff.remainingTurns() == 0 && !isPlayingFighter) {
            buff.incrementRemainingTurns();
        }

        fighter.fight().send(new AddBuff(buff));

        // Add one turn when it's the turn of the current fighter
        if (isPlayingFighter) {
            buff.incrementRemainingTurns();
        }
    }

    @Override
    public boolean onStartTurn() {
        boolean result = true;

        for (FightBuff buff : buffs) {
            result &= buff.hook().onStartTurn(buff);
        }

        return result;
    }

    @Override
    public void onEndTurn(Turn turn) {
        for (FightBuff buff : buffs) {
            buff.hook().onEndTurn(buff, turn);
        }
    }

    @Override
    public void onCast(FightCastScope cast) {
        for (FightBuff buff : buffs) {
            buff.hook().onCast(buff, cast);
        }
    }

    @Override
    public boolean onCastTarget(FightCastScope cast) {
        for (FightBuff buff : buffs) {
            if (!buff.hook().onCastTarget(buff, cast)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void onDirectDamage(Fighter caster, Damage value) {
        for (FightBuff buff : buffs) {
            buff.hook().onDirectDamage(buff, caster, value);
        }
    }

    @Override
    public void onIndirectDamage(Fighter caster, Damage value) {
        for (FightBuff buff : buffs) {
            buff.hook().onIndirectDamage(buff, caster, value);
        }
    }

    @Override
    public void onBuffDamage(FightBuff poison, Damage value) {
        for (FightBuff buff : buffs) {
            buff.hook().onBuffDamage(buff, poison, value);
        }
    }

    @Override
    public void onDirectDamageApplied(Fighter caster, @Positive int value) {
        for (FightBuff buff : buffs) {
            buff.hook().onDirectDamageApplied(buff, caster, value);
        }
    }

    @Override
    public void onHealApplied(@NonNegative int value) {
        for (FightBuff buff : buffs) {
            buff.hook().onHealApplied(buff, value);
        }
    }

    @Override
    public void onDamageApplied(@NonNegative int value) {
        for (FightBuff buff : buffs) {
            buff.hook().onDamageApplied(buff, value);
        }
    }

    @Override
    public void onElementDamageApplied(Element element, @NonNegative int actualDamage) {
        for (FightBuff buff : buffs) {
            buff.hook().onElementDamageApplied(buff, element, actualDamage);
        }
    }

    @Override
    public void onReflectedDamage(ReflectedDamage damage) {
        for (FightBuff buff : buffs) {
            buff.hook().onReflectedDamage(buff, damage);
        }
    }

    @Override
    public void onCastDamage(Damage damage, Fighter target) {
        for (FightBuff buff : buffs) {
            buff.hook().onCastDamage(buff, damage, target);
        }
    }

    @Override
    public void onEffectValueCast(EffectValue value) {
        for (FightBuff buff : buffs) {
            buff.hook().onEffectValueCast(buff, value);
        }
    }

    @Override
    public void onEffectValueTarget(EffectValue value) {
        for (FightBuff buff : buffs) {
            buff.hook().onEffectValueTarget(buff, value);
        }
    }

    @Override
    public void onCharacteristicAltered(Characteristic characteristic, int value) {
        for (FightBuff buff : buffs) {
            buff.hook().onCharacteristicAltered(buff, characteristic, value);
        }
    }

    @Override
    public void refresh() {
        // invalidate buffs before removing it in case of buffs are iterated by onBuffTerminated() hook
        buffs.forEach(FightBuff::decrementRemainingTurns);
        removeIf(buff -> !buff.valid());
    }

    @Override
    public boolean removeAll() {
        return removeIf(FightBuff::canBeDispelled);
    }

    @Override
    public boolean removeByCaster(FighterData caster) {
        return removeIf(buff -> buff.caster().equals(caster));
    }

    /**
     * Remove buff by a predicate
     *
     * @param predicate Takes the buff as parameter, and return true to delete (and terminate) the buff
     *
     * @return true if there is a change (i.e. a buff is terminated)
     */
    private boolean removeIf(Predicate<FightBuff> predicate) {
        final Iterator<FightBuff> iterator = buffs.iterator();

        boolean hasChanged = false;

        while (iterator.hasNext()) {
            final FightBuff buff = iterator.next();

            if (predicate.test(buff)) {
                iterator.remove();
                buff.hook().onBuffTerminated(buff);
                hasChanged = true;
            }
        }

        return hasChanged;
    }
}
