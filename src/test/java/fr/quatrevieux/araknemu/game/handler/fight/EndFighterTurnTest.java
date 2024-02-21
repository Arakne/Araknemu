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
import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.network.game.in.fight.TurnEnd;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.FinishTurn;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.StartTurn;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.TurnMiddle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EndFighterTurnTest extends FightBaseCase {
    private EndFighterTurn handler;
    private Fight fight;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new EndFighterTurn();
        fight = createFight();
    }

    @Test
    void notActiveTurn() {
        requestStack.clear();
        handler.handle(session, new TurnEnd());
        requestStack.assertEmpty();
    }

    @Test
    void success() {
        fight.state(PlacementState.class).startFight();
        fight.turnList().start();
        requestStack.clear();

        FightTurn turn = player.fighter().turn();

        handler.handle(session, new TurnEnd());

        assertSame(other.fighter(), fight.turnList().current().get().fighter());
        assertThrows(FightException.class, () -> player.fighter().turn());

        requestStack.assertAll(
            new FinishTurn(turn),
            new TurnMiddle(fight.fighters()),
            new Stats(player.fighter().properties()),
            new StartTurn(other.fighter().turn())
        );
    }
}
