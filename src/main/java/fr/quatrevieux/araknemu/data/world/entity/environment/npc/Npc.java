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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.data.world.entity.environment.npc;

import fr.arakne.utils.maps.constant.Direction;
import fr.quatrevieux.araknemu.data.value.Position;
import org.checkerframework.dataflow.qual.Pure;

/**
 * Store the NPC data
 */
public final class Npc {
    private final int id;
    private final int templateId;
    private final Position position;
    private final Direction orientation;
    private final int[] questions;

    public Npc(int id, int templateId, Position position, Direction orientation, int[] questions) {
        this.id = id;
        this.templateId = templateId;
        this.position = position;
        this.orientation = orientation;
        this.questions = questions;
    }

    /**
     * Get the NPC unique id
     */
    public int id() {
        return id;
    }

    /**
     * Get the template id
     *
     * @see NpcTemplate#id()
     */
    public int templateId() {
        return templateId;
    }

    /**
     * Get the NPC position on map
     */
    @Pure
    public Position position() {
        return position;
    }

    /**
     * Get the NPC sprite orientation
     */
    public Direction orientation() {
        return orientation;
    }

    /**
     * Get list of available questions ids on the NPC
     *
     * @see Question#id()
     */
    public int[] questions() {
        return questions;
    }
}
