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

package fr.quatrevieux.araknemu.game.fight.fighter;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterCharacteristicChanged;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import org.checkerframework.checker.index.qual.NonNegative;

/**
 * Characteristics for a fighter
 */
public interface FighterCharacteristics extends Characteristics {
    /**
     * Get the fighter initiative
     */
    public int initiative();

    /**
     * Get the total discernment of the fighter
     */
    public @NonNegative int discernment();

    /**
     * Change a buff characteristic
     *
     * The event {@link FighterCharacteristicChanged} will be dispatched on the fighter, with the modified characteristic
     * and the modifier value as parameters.
     *
     * @param characteristic Characteristic to add
     * @param value The value of the effect. Positive valuer for add the characteristic, or negative to remove
     */
    public void alter(Characteristic characteristic, int value);

    /**
     * Get initial characteristic of the fighter
     * Those characteristics ignore fight buffs, and corresponds to characteristics of the fighter before start the fight
     */
    public Characteristics initial();

    /**
     * Modify the fighter discernment
     *
     * In case of invocation, this will allow the creature to get items on end fight
     * The discernment cannot be negative, so if the sum of the value and the current discernment is negative,
     * the discernment will be set to 0
     *
     * @param value The value to add to the discernment. If negative, remove the value.
     */
    public void alterDiscernment(int value);
}
