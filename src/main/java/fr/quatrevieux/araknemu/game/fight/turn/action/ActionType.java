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

package fr.quatrevieux.araknemu.game.fight.turn.action;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Enum for all fight actions
 */
public enum ActionType {
    NONE(0, 0),
    MOVE(1, 2),
    CAST(300, 0),
    CLOSE_COMBAT(303, 0);

    private static final Map<Integer, ActionType> actionsById = new HashMap<>();

    private final int id;
    private final int end;

    static {
        for (ActionType actionType : values()) {
            actionsById.put(actionType.id, actionType);
        }
    }

    ActionType(int id, int end) {
        this.id = id;
        this.end = end;
    }

    /**
     * The game action id
     */
    public int id() {
        return id;
    }

    /**
     * The end action id
     */
    public int end() {
        return end;
    }

    /**
     * Get an action by id
     *
     * @throws NoSuchElementException If the action type do not exist
     */
    public static ActionType byId(int id) {
        final ActionType type = actionsById.get(id);

        if (type != null) {
            return type;
        }

        throw new NoSuchElementException("ActionType " + id + " do not exist");
    }
}
