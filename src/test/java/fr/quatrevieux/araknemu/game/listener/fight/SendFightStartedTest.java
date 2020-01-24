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
import fr.quatrevieux.araknemu.game.fight.event.FightStarted;
import fr.quatrevieux.araknemu.game.fight.turn.order.AlternateTeamFighterOrder;
import fr.quatrevieux.araknemu.network.game.out.fight.BeginFight;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.FighterTurnOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SendFightStartedTest extends GameBaseCase {
    private SendFightStarted listener;
    private Fight fight;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();

        listener = new SendFightStarted(
            fight = container.get(FightService.class).handler(ChallengeBuilder.class).start(
                builder -> {
                    try {
                        builder
                            .fighter(gamePlayer(true))
                            .fighter(makeOtherPlayer())
                            .map(container.get(ExplorationMapService.class).load(10340))
                        ;
                    } catch (Exception e) {
                        e.printStackTrace();
                        fail(e);
                    }
                }
            )
        );

        fight.turnList().init(new AlternateTeamFighterOrder());
        requestStack.clear();
    }

    @Test
    void onFightStarted() {
        listener.on(new FightStarted(fight));

        requestStack.assertAll(
            new BeginFight(),
            new FighterTurnOrder(fight.turnList())
        );
    }
}