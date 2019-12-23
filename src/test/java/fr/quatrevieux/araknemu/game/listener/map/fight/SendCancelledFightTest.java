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

package fr.quatrevieux.araknemu.game.listener.map.fight;

import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.event.FightCancelled;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.FightsCount;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.HideFight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SendCancelledFightTest extends FightBaseCase {
    private Fight fight;
    private ExplorationMap map;
    private SendCancelledFight listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();

        map = container.get(ExplorationMapService.class).load(10340);
        explorationPlayer().join(map);

        fight = createSimpleFight(map);
        listener = new SendCancelledFight(map, container.get(FightService.class));
        requestStack.clear();
    }

    @Test
    void onFightCancelled() {
        listener.on(new FightCancelled(fight));

        requestStack.assertAll(
            new HideFight(fight),
            new FightsCount(1)
        );
    }
}
