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
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.fighter.invocation;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterCharacteristics;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterCharacteristicChanged;
import fr.quatrevieux.araknemu.game.monster.Monster;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;

/**
 * Characteristics for an invocation
 * Element characteristics (i.e. strength, intelligence, agility, luck) are altered by the invoker level, each level given 1% boost
 */
public final class InvocationFighterCharacteristics implements FighterCharacteristics {
    private final Characteristics base;
    private final Fighter fighter;
    private final MutableCharacteristics boost;
    private final float rate;

    public InvocationFighterCharacteristics(Monster monster, Fighter fighter, PassiveFighter invoker) {
        this.base = monster.characteristics();
        this.fighter = fighter;
        this.boost = new DefaultCharacteristics();
        rate = modifier(invoker);
    }

    @Override
    public int initiative() {
        return 0;
    }

    @Override
    public int discernment() {
        return 0;
    }

    @Override
    public void alter(Characteristic characteristic, int value) {
        boost.add(characteristic, value);
        fighter.dispatch(new FighterCharacteristicChanged(characteristic, value));
    }

    @Override
    public Characteristics initial() {
        return base;
    }

    @Override
    public int get(Characteristic characteristic) {
        int value = base.get(characteristic);

        if (characteristic == Characteristic.STRENGTH
            || characteristic == Characteristic.INTELLIGENCE
            || characteristic == Characteristic.LUCK
            || characteristic == Characteristic.AGILITY
        ) {
            value *= rate;
        }

        return value + boost.get(characteristic);
    }

    /**
     * Get the characteristics modifier for the given invoke
     * The value is always higher than 1
     */
    public static float modifier(PassiveFighter invoker) {
        return 1 + invoker.level() / 100f;
    }
}
