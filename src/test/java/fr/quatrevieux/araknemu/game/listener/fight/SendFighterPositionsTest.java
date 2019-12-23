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

package fr.quatrevieux.araknemu.game.listener.fight;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.builder.ChallengeBuilder;
import fr.quatrevieux.araknemu.game.fight.event.FighterPlaceChanged;
import fr.quatrevieux.araknemu.network.game.out.fight.FighterPositions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SendFighterPositionsTest extends GameBaseCase {
    private Fight fight;
    private SendFighterPositions listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();

        fight = container.get(FightService.class).handler(ChallengeBuilder.class)
            .start(builder -> {
                try {
                    builder
                        .fighter(gamePlayer(true))
                        .fighter(makeOtherPlayer())
                        .map(container.get(ExplorationMapService.class).load(10340))
                    ;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            })
        ;

        listener = new SendFighterPositions(fight);
        requestStack.clear();
    }

    @Test
    void onFighterPlaceChanged() {
        listener.on(new FighterPlaceChanged(fight.fighters().get(0)));

        requestStack.assertLast(new FighterPositions(fight.fighters()));
    }
}