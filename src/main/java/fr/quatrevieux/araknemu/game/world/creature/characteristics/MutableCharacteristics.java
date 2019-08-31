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

package fr.quatrevieux.araknemu.game.world.creature.characteristics;

import fr.quatrevieux.araknemu.data.constant.Characteristic;

/**
 * Interface for mutable characteristics map
 */
public interface MutableCharacteristics extends Characteristics {
    /**
     * Set value for the given characteristic
     */
    public void set(Characteristic characteristic, int value);

    /**
     * Add the characteristic value to the characteristics map
     *
     * @param characteristic Characteristic to modify
     * @param value The value to add
     */
    public void add(Characteristic characteristic, int value);
}
