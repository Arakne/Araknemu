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

package fr.quatrevieux.araknemu.game.listener.map;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.event.CreatureMoving;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;

/**
 * Send to the map the sprite move
 */
public final class SendCreatureMove implements Listener<CreatureMoving> {
    private final ExplorationMap map;

    public SendCreatureMove(ExplorationMap map) {
        this.map = map;
    }

    @Override
    public void on(CreatureMoving event) {
        map.send(new GameActionResponse("", ActionType.MOVE, event.creature().id(), event.path().encode()));
    }

    @Override
    public Class<CreatureMoving> event() {
        return CreatureMoving.class;
    }
}
