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

package fr.quatrevieux.araknemu.game.handler.fight;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.network.game.in.fight.ListFightsRequest;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.FightList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class ListFightsTest extends FightBaseCase {
    private ListFights handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new ListFights(container.get(FightService.class));
    }

    @Test
    void withoutFights() throws SQLException, ContainerException {
        explorationPlayer();

        handler.handle(session, new ListFightsRequest());

        requestStack.assertLast(new FightList(Collections.emptyList()));
    }

    @Test
    void fightsOnBadMap() throws SQLException, ContainerException {
        explorationPlayer();

        createSimpleFight(container.get(ExplorationMapService.class).load(10340));
        createSimpleFight(container.get(ExplorationMapService.class).load(10340));

        handler.handle(session, new ListFightsRequest());

        requestStack.assertLast(new FightList(Collections.emptyList()));
    }

    @Test
    void fightsOnCurrentMap() throws SQLException, ContainerException {
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);
        explorationPlayer().changeMap(map, 123);

        List<Fight> fights = Arrays.asList(
            createSimpleFight(map),
            createSimpleFight(map)
        );

        handler.handle(session, new ListFightsRequest());

        requestStack.assertLast(new FightList(fights));
    }
}
