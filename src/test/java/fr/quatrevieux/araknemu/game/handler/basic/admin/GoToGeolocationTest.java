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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.handler.basic.admin;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.core.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.core.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.data.value.Geolocation;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.network.game.in.basic.admin.AdminMove;
import fr.quatrevieux.araknemu.network.game.out.basic.Noop;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 *
 */
class GoToGeolocationTest extends GameBaseCase {
    @Test
    void teleportSuccess() throws Exception {
        ExplorationPlayer player = explorationPlayer();
        player.changeCell(250);
        player.account().grant(Permission.ACCESS);

        handlePacket(new AdminMove(new Geolocation(3, 6)));

        assertEquals(10340, player.map().id());
        assertEquals(250, player.cell().id());
    }

    @Test
    void teleportSuccessWithInvalidTargetCellShouldFindFirstWalkableCell() throws Exception {
        ExplorationPlayer player = explorationPlayer();
        player.account().grant(Permission.ACCESS);

        handlePacket(new AdminMove(new Geolocation(3, 6)));

        assertEquals(10340, player.map().id());
        assertEquals(15, player.cell().id());
        assertTrue(player.cell().walkable());
    }

    @Test
    void teleportNotAdmin() throws Exception {
        ExplorationPlayer player = explorationPlayer();

        try {
            handlePacket(new AdminMove(new Geolocation(3, 6)));
            fail("Expects ErrorPacket");
        } catch (ErrorPacket e) {
            assertEquals(new Noop().toString(), e.packet().toString());
        }

        assertNotEquals(10340, player.map().id());
    }

    @Test
    void teleportNotExploring() {
        assertThrows(CloseImmediately.class, () -> handlePacket(new AdminMove(new Geolocation(3, 6))));
    }
}
