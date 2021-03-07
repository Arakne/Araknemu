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

package fr.quatrevieux.araknemu.data.world.entity.monster;

import fr.quatrevieux.araknemu.data.value.Position;

/**
 * Spawn position for the monster group on map
 */
public final class MonsterGroupPosition {
    private final Position position;
    private final int groupId;

    public MonsterGroupPosition(Position position, int groupId) {
        this.position = position;
        this.groupId = groupId;
    }

    /**
     * The group spawn position
     * The cell is not required : If the value is -1, the group may spawn in any free free
     *
     * The position is the primary key : this value is unique
     */
    public Position position() {
        return position;
    }

    /**
     * Foreign key for monster group id
     *
     * @see MonsterGroupData#id()
     */
    public int groupId() {
        return groupId;
    }
}
