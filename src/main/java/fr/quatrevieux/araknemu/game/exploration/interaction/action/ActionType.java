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

package fr.quatrevieux.araknemu.game.exploration.interaction.action;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum for all game actions
 */
public enum ActionType {
    NONE(0),
    MOVE(1),
    CHANGE_MAP(2),
    FIREWORK(228),
    CHALLENGE(900),
    ACCEPT_CHALLENGE(901),
    REFUSE_CHALLENGE(902),
    JOIN_FIGHT(903);

    final static private Map<Integer, ActionType> actionsById = new HashMap<>();

    final private int id;

    static {
        for (ActionType actionType : values()) {
            actionsById.put(actionType.id, actionType);
        }
    }

    ActionType(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    /**
     * Get an action by id
     */
    static public ActionType byId(int id) {
        return actionsById.get(id);
    }
}
