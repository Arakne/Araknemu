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
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;

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
    public int discernment();

    /**
     * Change a buff characteristic
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
}
