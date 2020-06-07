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

package fr.quatrevieux.araknemu.game.fight.state;

import fr.arakne.utils.maps.serializer.CellData;
import fr.arakne.utils.value.Dimensions;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StatesFlowTest extends GameBaseCase {
    @Test
    void flow() {
        FightState s1 = Mockito.mock(FightState.class);
        FightState s2 = Mockito.mock(FightState.class);

        StatesFlow flow = new StatesFlow(s1, s2);

        assertSame(s1, flow.current());

        Fight fight = new Fight(
            1,
            new ChallengeType(),
            new FightMap(
                new MapTemplate(0, "", new Dimensions(0, 0), "", new CellData[0], new List[0], null, 0, false)
            ),
            new ArrayList<>(),
            new StatesFlow(),
            container.get(Logger.class)
        );

        flow.next(fight);

        assertSame(s2, flow.current());
        Mockito.verify(s2).start(fight);
    }
}