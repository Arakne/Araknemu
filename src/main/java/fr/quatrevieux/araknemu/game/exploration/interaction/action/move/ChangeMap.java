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

package fr.quatrevieux.araknemu.game.exploration.interaction.action.move;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.Action;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionQueue;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;

/**
 * Change current map after a move
 */
public final class ChangeMap implements Action {
    private final ExplorationPlayer player;
    private final ExplorationMap map;
    private final int cell;
    private final int cinematic;

    public ChangeMap(ExplorationPlayer player, ExplorationMap map, int cell, int cinematic) {
        this.player = player;
        this.map = map;
        this.cell = cell;
        this.cinematic = cinematic;
    }

    public ChangeMap(ExplorationPlayer player, ExplorationMap map, int cell) {
        this(player, map, cell, 0);
    }

    @Override
    public void start(ActionQueue queue) {
        player.leave();
        player.changeMap(map, cell);

        player.send(new GameActionResponse(this));
    }

    @Override
    public ExplorationPlayer performer() {
        return player;
    }

    @Override
    public ActionType type() {
        return ActionType.CHANGE_MAP;
    }

    @Override
    public Object[] arguments() {
        return new Object[] {cinematic != 0 ? cinematic : ""};
    }
}
