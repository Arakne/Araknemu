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

package fr.quatrevieux.araknemu.network.game.out.fight.turn;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.builder.ChallengeBuilder;
import fr.quatrevieux.araknemu.game.fight.turn.order.AlternateTeamFighterOrder;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FighterTurnOrderTest extends GameBaseCase {
    private Fight fight;
    private GamePlayer p1, p2;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();

        p1 = gamePlayer(true);
        p2 = makeOtherPlayer();

        fight = container.get(FightService.class).handler(ChallengeBuilder.class)
            .start(
                builder -> {
                    try {
                        builder
                            .fighter(p1)
                            .fighter(p2)
                            .map(container.get(ExplorationMapService.class).load(10340))
                        ;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            )
        ;

        fight.turnList().init(new AlternateTeamFighterOrder());
    }

    @Test
    void generate() {
        assertEquals(
            "GTL|" + p1.id() + "|" + p2.id(),
            new FighterTurnOrder(fight.turnList()).toString()
        );
    }
}