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

package fr.quatrevieux.araknemu.game.item;

import java.util.NoSuchElementException;

/**
 * Item super types
 */
public enum SuperType {
    NONE,
    AMULET,
    WEAPON,
    RING,
    BELT,
    BOOT,
    USABLE,
    SHIELD,
    CAPTURE_WEAPON,
    RESOURCE,
    HELMET,
    MANTLE,
    PET,
    DOFUS,
    QUEST,
    MUTATION,
    BOOST,
    BLESSING,
    CURSE,
    BUFF,
    FOLLOWER,
    MOUNT,
    LIVING_OBJECT,
    ;

    private static final SuperType[] VALUES = values();

    /**
     * Get a SupeType by its id
     * This id can be found into `items_xx_xxx.swf`, as key of `I.st` object
     *
     * @throws NoSuchElementException If the type is not found
     */
    public static SuperType byId(int id) {
        if (id < 0 || id >= VALUES.length) {
            throw new NoSuchElementException("Invalid super type " + id);
        }

        return VALUES[id];
    }
}
