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
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.fighter;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterCharacteristicChanged;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;

/**
 * Base type for handle fighter characteristics
 * This class implements buffs boost and dispatches events
 */
public abstract class AbstractFighterCharacteristics implements FighterCharacteristics {
    private final Fighter fighter;
    private final Characteristics base;
    private final MutableCharacteristics buffs = new DefaultCharacteristics();

    public AbstractFighterCharacteristics(Fighter fighter, Characteristics base) {
        this.fighter = fighter;
        this.base = base;
    }

    @Override
    public final void alter(Characteristic characteristic, int value) {
        buffs.add(characteristic, value);
        fighter.dispatch(new FighterCharacteristicChanged(characteristic, value));
    }

    @Override
    public final Characteristics initial() {
        return base;
    }

    @Override
    public final int get(Characteristic characteristic) {
        return base.get(characteristic) + buffs.get(characteristic);
    }

    /**
     * Get the temporary characteristics (i.e. buffs)
     */
    protected final Characteristics buffs() {
        return buffs;
    }
}
