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

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.admin.AdminService;
import fr.quatrevieux.araknemu.game.admin.AdminUser;
import fr.quatrevieux.araknemu.game.admin.LogType;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.network.game.out.basic.admin.CommandResult;
import fr.quatrevieux.araknemu.network.game.out.game.FightStartPositions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;

class FightPosTest extends GameBaseCase {
    private FightPos command;
    private AdminUser user;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();
        ExplorationPlayer player = explorationPlayer();
        player.leave();
        player.changeMap(container.get(ExplorationMapService.class).load(10340), 200);

        command = new FightPos();
        user = container.get(AdminService.class).user(gamePlayer());

        requestStack.clear();
    }

    @Test
    void hide() throws AdminException {
        command.execute(user, Arrays.asList("fightpos", "hide"));

        requestStack.assertLast("GV");
    }

    @Test
    void noFightPos() throws AdminException, SQLException {
        explorationPlayer().changeMap(container.get(ExplorationMapService.class).load(10300), 200);

        command.execute(user, Arrays.asList("fightpos"));

        requestStack.assertLast(
            new CommandResult(LogType.ERROR, "No fight places found")
        );
    }

    @Test
    void noDisplay() throws AdminException {
        command.execute(user, Arrays.asList("fightpos"));

        requestStack.assertLast(
            new CommandResult(LogType.DEFAULT, "Places : [55, 83, 114, 127, 128, 170, 171, 183, 185, 198] | [48, 63, 75, 90, 92, 106, 121, 122, 137, 150]")
        );
    }

    @Test
    void show() throws AdminException, ContainerException {
        command.execute(user, Arrays.asList("fightpos", "show"));

        requestStack.assertAll(
            new CommandResult(LogType.DEFAULT, "Places : [55, 83, 114, 127, 128, 170, 171, 183, 185, 198] | [48, 63, 75, 90, 92, 106, 121, 122, 137, 150]"),
            new FightStartPositions(container.get(MapTemplateRepository.class).get(10340).fightPlaces(), 0)
        );
    }
}
