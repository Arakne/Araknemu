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

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.event.PlayerMoveFinished;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.teleport.Teleport;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 */
class PerformCellActionsTest extends GameBaseCase {
    private PerformCellActions listener;
    private ExplorationMap map;
    private ExplorationPlayer player;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();
        dataSet.pushTrigger(new MapTrigger(10300, 120, Teleport.ACTION_ID, "10540,156", ""));
        listener = new PerformCellActions();
        map = container.get(ExplorationMapService.class).load(10300);
        player = explorationPlayer();
        requestStack.clear();
    }

    @Test
    void onBasicCellDoNothing() {
        listener.on(new PlayerMoveFinished(player, map.get(456)));

        requestStack.assertEmpty();
        assertFalse(player.interactions().busy());
    }

    @Test
    void onNonEmptyCell() throws SQLException, ContainerException {
        listener.on(new PlayerMoveFinished(player, map.get(120)));

        assertTrue(player.interactions().busy());
        requestStack.assertLast(
            new GameActionResponse("1", ActionType.CHANGE_MAP, explorationPlayer().id(), "")
        );
    }
}
