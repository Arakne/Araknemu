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

package fr.quatrevieux.araknemu.game.world.creature.accessory;

/**
 * Interface for sprite accessory
 */
public interface Accessory {
    /**
     * Get the accessory type
     */
    public AccessoryType type();

    /**
     * Get the accessory appearance
     */
    public int appearance();

    /**
     * Get the accessory item type
     * This method return -1 to use the appearance type
     */
    public default int itemType() {
        return -1;
    }

    /**
     * Get the accessory display frame
     * This method must return 0 if not custom item type is given
     */
    public default int frame() {
        return 0;
    }
}
