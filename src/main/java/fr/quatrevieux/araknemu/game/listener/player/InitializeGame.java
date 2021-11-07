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

package fr.quatrevieux.araknemu.game.listener.player;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.event.StartExploration;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;

/**
 * Initialize game for the player
 */
public final class InitializeGame implements Listener<StartExploration> {
    private final ExplorationPlayer player;
    private final ExplorationMapService mapService;

    public InitializeGame(ExplorationPlayer player, ExplorationMapService mapService) {
        this.player = player;
        this.mapService = mapService;
    }

    @Override
    public void on(StartExploration event) {
        player.send(new Stats(player.properties()));

        player.join(
            mapService.load(player.position().map()) // @todo handle entity not found
        );
    }

    @Override
    public Class<StartExploration> event() {
        return StartExploration.class;
    }
}
