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

package fr.quatrevieux.araknemu.game.admin.debug;

import fr.arakne.utils.maps.MapCell;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import fr.quatrevieux.araknemu.game.admin.LogType;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.network.game.out.basic.admin.CommandResult;
import fr.quatrevieux.araknemu.network.game.out.game.FightStartPositions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class FightPosTest extends CommandTestCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();
        ExplorationPlayer player = explorationPlayer();
        player.leave();
        player.changeMap(container.get(ExplorationMapService.class).load(10340), 200);

        command = new FightPos();

        requestStack.clear();
    }

    @Test
    void hide() throws AdminException, SQLException {
        executeWithAdminUser("fightpos", "hide");

        requestStack.assertLast("GV");
    }

    @Test
    void notOnMap() throws SQLException, AdminException {
        explorationPlayer().leave();

        executeWithAdminUser("fightpos");
        requestStack.assertLast(
            new CommandResult(LogType.ERROR, "The player is not on map")
        );
    }

    @Test
    void noFightPos() throws AdminException, SQLException {
        explorationPlayer().changeMap(container.get(ExplorationMapService.class).load(10300), 200);

        executeWithAdminUser("fightpos");

        requestStack.assertLast(
            new CommandResult(LogType.ERROR, "No fight places found")
        );
    }

    @Test
    void noDisplay() throws AdminException, SQLException {
        executeWithAdminUser("fightpos");

        requestStack.assertLast(
            new CommandResult(LogType.DEFAULT, "Places : [55, 83, 114, 127, 128, 170, 171, 183, 185, 198] | [48, 63, 75, 90, 92, 106, 121, 122, 137, 150]")
        );
    }

    @Test
    void show() throws AdminException, ContainerException, SQLException {
        executeWithAdminUser("fightpos", "show");

        requestStack.assertAll(
            new CommandResult(LogType.DEFAULT, "Places : [55, 83, 114, 127, 128, 170, 171, 183, 185, 198] | [48, 63, 75, 90, 92, 106, 121, 122, 137, 150]"),
            new FightStartPositions(new MapCell[][] {
                explorationPlayer().map().fightPlaces(0).toArray(new MapCell[0]),
                explorationPlayer().map().fightPlaces(1).toArray(new MapCell[0])
            }, 0)
        );
    }
}
