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
import fr.quatrevieux.araknemu.game.fight.event.FighterAdded;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.AddTeamFighters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

class SendTeamFighterAddedTest extends FightBaseCase {
    private Fight fight;
    private ExplorationMap map;
    private SendTeamFighterAdded listener;
    private PlayerFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();

        map = container.get(ExplorationMapService.class).load(10340);
        explorationPlayer().join(map);

        fight = createSimpleFight(map);
        listener = new SendTeamFighterAdded(map);

        fighter = makePlayerFighter(makeSimpleGamePlayer(10));

        requestStack.clear();
    }

    @Test
    void onFighterAdded() {
        fighter.setTeam(fight.team(0));

        listener.on(new FighterAdded(fighter));

        requestStack.assertLast(new AddTeamFighters(fight.team(0), Collections.singleton(fighter)));
    }
}