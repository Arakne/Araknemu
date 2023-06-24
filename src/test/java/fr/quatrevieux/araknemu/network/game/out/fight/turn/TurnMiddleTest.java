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

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.turn.order.AlternateTeamFighterOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TurnMiddleTest extends FightBaseCase {
    private Fight fight;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
    }

    @Test
    void generate() {
        assertEquals(
            "GTM|1;0;295;6;3;122;;295|2;0;50;6;3;125;;50",
            new TurnMiddle(fight.fighters()).toString()
        );
    }

    @Test
    void generateWithHiddenFighterShouldNotSendCell() {
        other.fighter().setHidden(other.fighter(), true);

        assertEquals(
            "GTM|1;0;295;6;3;122;;295|2;0;50;6;3;-1;;50",
            new TurnMiddle(fight.fighters()).toString()
        );
    }

    @Test
    void generateWithActiveTurnShouldShowTurnPoints() {
        fight.start(new AlternateTeamFighterOrder());
        fight.turnList().start();

        fight.turnList().current().get().points().useActionPoints(3);
        fight.turnList().current().get().points().useMovementPoints(1);

        assertEquals(
            "GTM|1;0;295;3;2;122;;295|2;0;50;6;3;125;;50",
            new TurnMiddle(fight.fighters()).toString()
        );
    }

    @Test
    void generateWithDeadFighter() {
        player.fighter().init();
        player.fighter().life().alter(player.fighter(), -1000);

        assertEquals(
            "GTM|1;1|2;0;50;6;3;125;;50",
            new TurnMiddle(fight.fighters()).toString()
        );
    }
}