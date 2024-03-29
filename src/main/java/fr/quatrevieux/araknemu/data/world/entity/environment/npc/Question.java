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

package fr.quatrevieux.araknemu.data.world.entity.environment.npc;

import org.checkerframework.common.value.qual.MinLen;
import org.checkerframework.dataflow.qual.Pure;

/**
 * Store NPC questions data
 */
public final class Question {
    private final int id;
    private final int[] responseIds;
    private final @MinLen(1) String[] parameters;
    private final String condition;

    public Question(int id, int[] responseIds, @MinLen(1) String[] parameters, String condition) {
        this.id = id;
        this.responseIds = responseIds;
        this.parameters = parameters;
        this.condition = condition;
    }

    /**
     * The question ID
     * Should corresponds with dialog SWF
     */
    @Pure
    public int id() {
        return id;
    }

    /**
     * List of available response ids
     */
    @Pure
    public int[] responseIds() {
        return responseIds;
    }

    /**
     * Get the question variable / parameters like name
     */
    @Pure
    public @MinLen(1) String[] parameters() {
        return parameters;
    }

    /**
     * Get the question condition
     * If not match, the next question should be asked
     */
    @Pure
    public String condition() {
        return condition;
    }
}
