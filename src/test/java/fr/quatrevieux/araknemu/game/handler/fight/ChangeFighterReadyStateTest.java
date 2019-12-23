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

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.builder.ChallengeBuilder;
import fr.quatrevieux.araknemu.game.fight.state.ActiveState;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.in.fight.FighterReady;
import fr.quatrevieux.araknemu.network.game.out.fight.BeginFight;
import fr.quatrevieux.araknemu.network.game.out.fight.FighterReadyState;
import io.github.artsok.RepeatedIfExceptionsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChangeFighterReadyStateTest extends GameBaseCase {
    private ChangeFighterReadyState handler;
    private Fight fight;
    private GamePlayer other;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();

        handler = new ChangeFighterReadyState();

        GamePlayer player = gamePlayer(true);
        other = makeOtherPlayer();
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        fight = container.get(FightService.class).handler(ChallengeBuilder.class)
            .start(
                builder -> {
                    builder
                        .fighter(player)
                        .fighter(other)
                        .map(map);
                }
            );
    }

    @Test
    void setReady() {
        handler.handle(session, new FighterReady(true));

        requestStack.assertLast(new FighterReadyState(session.fighter()));
        assertTrue(session.fighter().ready());
    }

    @Test
    void unsetReady() {
        session.fighter().setReady(true);
        requestStack.clear();

        handler.handle(session, new FighterReady(false));

        requestStack.assertLast(new FighterReadyState(session.fighter()));
        assertFalse(session.fighter().ready());
    }

    @RepeatedIfExceptionsTest
    void setReadyAndStartFight() throws InterruptedException {
        other.fighter().setReady(true);

        handler.handle(session, new FighterReady(true));
        Thread.sleep(210);

        requestStack.assertOne(new BeginFight());
        assertInstanceOf(ActiveState.class, fight.state());
    }
}