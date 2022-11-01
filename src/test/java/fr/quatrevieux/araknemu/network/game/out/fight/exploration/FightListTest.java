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

package fr.quatrevieux.araknemu.network.game.out.fight.exploration;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.turn.order.AlternateTeamFighterOrder;
import fr.quatrevieux.araknemu.util.DofusDate;
import io.github.artsok.RepeatedIfExceptionsTest;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class FightListTest extends FightBaseCase {
    @Test
    void generateNotActive() throws ContainerException, SQLException {
        dataSet.pushMaps().pushSubAreas().pushAreas();
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        assertEquals(
            "fL1;-1;0,-1,1;0,-1,1|2;-1;0,-1,1;0,-1,1",
            new FightList(Arrays.asList(
                createSimpleFight(map),
                createSimpleFight(map)
            )).toString()
        );
    }

    @RepeatedIfExceptionsTest
    void generateActive() throws SQLException, ContainerException {
        dataSet.pushMaps().pushSubAreas().pushAreas();
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        Fight fight = createSimpleFight(map);

        fight.start(new AlternateTeamFighterOrder());

        long time = DofusDate.fromDuration(fight.duration()).toMilliseconds();

        String packet = new FightList(Collections.singleton(fight)).toString();

        for (int i = 0; i < 10; ++i) {
            if (packet.equals("fL1;" + (time + i) + ";0,-1,1;0,-1,1")) {
                return;
            }
        }

        fail(packet + " != fL1;" + time + ";0,-1,1;0,-1,1");
    }
}