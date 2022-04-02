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
import fr.quatrevieux.araknemu.core.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.core.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.in.fight.FighterChangePlace;
import fr.quatrevieux.araknemu.network.game.out.fight.ChangeFighterPlaceError;
import fr.quatrevieux.araknemu.network.game.out.fight.FighterPositions;
import io.github.artsok.RepeatedIfExceptionsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class ChangeFighterStartPlaceTest extends FightBaseCase {
    private ChangeFighterStartPlace handler;
    private Fight fight;
    private PlayerFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();
        handler = new ChangeFighterStartPlace();
    }

    @Test
    void handleError() throws Exception {
        fight = createFight();
        fighter = player.fighter();

        try {
            handler.handle(session, new FighterChangePlace(256));
            fail("ErrorPacket must be thrown");
        } catch (ErrorPacket e) {
            assertEquals(
                new ChangeFighterPlaceError().toString(),
                e.packet().toString()
            );
        }
    }

    @Test
    void handleInvalidCell() throws Exception {
        fight = createFight();
        fighter = player.fighter();

        try {
            handler.handle(session, new FighterChangePlace(1000));
            fail("ErrorPacket must be thrown");
        } catch (ErrorPacket e) {
            assertEquals(
                new ChangeFighterPlaceError().toString(),
                e.packet().toString()
            );
        }
    }

    @Test
    void handleSuccess() throws Exception {
        fight = createFight();
        fighter = player.fighter();

        handler.handle(session, new FighterChangePlace(123));

        requestStack.assertLast(new FighterPositions(fight.fighters()));

        assertEquals(123, fighter.cell().id());
    }

    @Test
    void functionalNotInFight() {
        assertThrows(CloseImmediately.class, () -> handlePacket(new FighterChangePlace(123)));
    }

    @RepeatedIfExceptionsTest
    void functionalSuccess() throws Exception {
        fight = createFight();
        fighter = player.fighter();

        handlePacket(new FighterChangePlace(123));

        assertEquals(123, fighter.cell().id());
    }
}
