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

/**
 * Action to perform for a response
 */
public final class ResponseAction {
    private final int responseId;
    private final String action;
    private final String arguments;

    public ResponseAction(int responseId, String action, String arguments) {
        this.responseId = responseId;
        this.action = action;
        this.arguments = arguments;
    }

    /**
     * Get the response id
     * The id is located into swf dialog_xx.swf into D.a[id]
     * This is not the primary key (i.e. not unique), but a part with action
     */
    public int responseId() {
        return responseId;
    }

    /**
     * Get the action name to perform with the response
     * This action is part of the primary key (with responseId)
     * This means that the same action cannot be performed more than once for a response
     */
    public String action() {
        return action;
    }

    /**
     * Get the action arguments
     * The format depends of the action
     */
    public String arguments() {
        return arguments;
    }
}
