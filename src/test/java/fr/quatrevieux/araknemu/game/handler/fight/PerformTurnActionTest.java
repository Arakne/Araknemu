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

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionRequest;
import fr.quatrevieux.araknemu.network.game.out.fight.action.NoneAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

class PerformTurnActionTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter fighter;
    private PerformTurnAction handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.nextState();
        handler = new PerformTurnAction();
        fighter = player.fighter();

        other.fighter().move(fight.map().get(123));
        fighter.move(fight.map().get(185));
    }

    @Test
    void moveSuccess() throws Exception {
        FightTurn turn = new FightTurn(fighter, fight, Duration.ofSeconds(30));
        turn.start();

        requestStack.clear();

        handler.handle(session, new GameActionRequest(1, new String[] {"ddvfdg"}));

        requestStack.assertAll(
            "GAS1",
            "GA0;1;1;ac5ddvfdg"
        );
    }

    @Test
    void moveInvalid() throws Exception {
        FightTurn turn = new FightTurn(fighter, fight, Duration.ofSeconds(30));
        turn.start();

        requestStack.clear();
        handler.handle(session, new GameActionRequest(1, new String[]{"ddvedg"}));
        requestStack.assertLast(new NoneAction());
    }

    @Test
    void notActiveTurn() throws Exception {
        handler.handle(session, new GameActionRequest(1, new String[]{"ddvedg"}));
        requestStack.assertLast(new NoneAction());
    }
}
