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

package fr.quatrevieux.araknemu.game.fight.fighter.invocation;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterCharacteristics;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterCharacteristicChanged;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;

/**
 * Characteristics for a double
 * This implements will simply retrieve the base characteristics from the invoker (i.e. {@link FighterCharacteristics#initial()})
 * and handle buffs.
 */
public final class DoubleFighterCharacteristics implements FighterCharacteristics {
    private final Fighter fighter;
    private final Characteristics base;

    private final MutableCharacteristics buffs = new DefaultCharacteristics();

    /**
     * @param fighter The double fighter
     * @param invoker The invoker
     */
    public DoubleFighterCharacteristics(Fighter fighter, Fighter invoker) {
        this.fighter = fighter;
        this.base = invoker.characteristics().initial();
    }

    @Override
    public int initiative() {
        return 0; // initiative is not used for invocations
    }

    @Override
    public int discernment() {
        return 0; // monster do not have discernment
    }

    @Override
    public int get(Characteristic characteristic) {
        return base.get(characteristic) + buffs.get(characteristic);
    }

    @Override
    public void alter(Characteristic characteristic, int value) {
        buffs.add(characteristic, value);
        fighter.dispatch(new FighterCharacteristicChanged(characteristic, value));
    }

    @Override
    public Characteristics initial() {
        return base;
    }
}
