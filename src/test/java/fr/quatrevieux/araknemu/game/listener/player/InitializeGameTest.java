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

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.event.StartExploration;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InitializeGameTest extends GameBaseCase {
    private InitializeGame listener;
    private ExplorationPlayer player;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new InitializeGame(
            player = new ExplorationPlayer(
                gamePlayer()
            ),
            container.get(ExplorationMapService.class)
        );

        dataSet.pushMaps().pushSubAreas().pushAreas();
    }

    @Test
    void onStartExploration() throws SQLException, ContainerException {
        listener.on(new StartExploration(player));

        requestStack.assertAll(new Stats(player.properties()));

        assertNotNull(player.map());
        assertEquals(10540, player.map().id());
    }
}
