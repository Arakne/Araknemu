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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.listener.player;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.player.experience.event.PlayerLevelUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RestoreLifePointsOnLevelUpTest extends GameBaseCase {
    private RestoreLifePointsOnLevelUp listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new RestoreLifePointsOnLevelUp(gamePlayer(true));
    }

    @Test
    void onLevelUp() throws SQLException {
        gamePlayer().properties().life().set(4);

        listener.on(new PlayerLevelUp(15));

        assertTrue(gamePlayer().properties().life().isFull());
    }

    @Test
    void functional() throws SQLException {
        gamePlayer().properties().life().set(4);
        gamePlayer().properties().experience().add(10000000);

        assertTrue(gamePlayer().properties().life().isFull());
    }
}
