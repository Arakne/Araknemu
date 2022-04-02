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

import org.checkerframework.checker.index.qual.GTENegativeOne;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.dataflow.qual.Pure;
import org.checkerframework.framework.qual.EnsuresQualifierIf;

/**
 * Spawn position for the monster group on map
 */
public final class MonsterGroupPosition {
    private final @NonNegative int map;
    private final @GTENegativeOne int cell;
    private final int groupId;

    public MonsterGroupPosition(@NonNegative int map, @GTENegativeOne int cell, int groupId) {
        this.map = map;
        this.cell = cell;
        this.groupId = groupId;
    }

    /**
     * The group spawn map id
     * This is part of primary key
     *
     * @see fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate#id()
     */
    @Pure
    public @NonNegative int map() {
        return map;
    }

    /**
     * The group spawn cell
     * The cell is not required : If the value is -1, the group may spawn on any free cell
     *
     * This is part of primary key
     */
    @Pure
    public @GTENegativeOne int cell() {
        return cell;
    }

    /**
     * Foreign key for monster group id
     *
     * @see MonsterGroupData#id()
     */
    @Pure
    public int groupId() {
        return groupId;
    }
}
