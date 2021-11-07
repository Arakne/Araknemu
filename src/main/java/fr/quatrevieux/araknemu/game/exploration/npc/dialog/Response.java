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

package fr.quatrevieux.araknemu.game.exploration.npc.dialog;

import fr.quatrevieux.araknemu.data.world.entity.environment.npc.ResponseAction;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.Action;

import java.util.List;

/**
 * Dialog response
 * The response will perform actions on the interlocutor
 */
public final class Response {
    private final int id;
    private final List<Action> actions;

    public Response(int id, List<Action> actions) {
        this.id = id;
        this.actions = actions;
    }

    /**
     * Get the response id
     * The id is located into swf dialog_xx.swf into D.a[id]
     *
     * @see ResponseAction#responseId()
     */
    public int id() {
        return id;
    }

    /**
     * Check if the response is valid
     *
     * A response is valid, if and only if all its actions can be performed
     * A response without actions cannot be considered as valid
     */
    public boolean check(ExplorationPlayer player) {
        return !actions.isEmpty() && actions.stream().allMatch(action -> action.check(player));
    }

    /**
     * Apply all actions to the player
     */
    public void apply(ExplorationPlayer player) {
        for (Action action : actions) {
            action.apply(player);
        }
    }
}
