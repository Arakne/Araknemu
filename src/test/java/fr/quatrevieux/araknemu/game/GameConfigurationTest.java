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

package fr.quatrevieux.araknemu.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class GameConfigurationTest extends GameBaseCase {
    private GameConfiguration configuration;

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        configuration = app.configuration().module(GameConfiguration.class);
    }

    @Test
    void values() {
        assertEquals(2, configuration.id());
        assertEquals(456, configuration.port());
        assertEquals("10.0.0.5", configuration.ip());
        assertEquals(Duration.ofMinutes(15), configuration.inactivityTime());
        assertEquals(100, configuration.packetRateLimit());
        assertArrayEquals(new long[] {1, 10, 30, 60, 120}, configuration.shutdownReminderMinutes());
        assertEquals(Duration.ofMinutes(10), configuration.banIpRefresh());
    }

    @Test
    void chat() {
        assertEquals(30, configuration.chat().floodTime());
        assertEquals("*#%!pi$:?", configuration.chat().defaultChannels());
        assertEquals("@", configuration.chat().adminChannels());
    }

    @Test
    void player() {
        assertEquals(20, configuration.player().deleteAnswerLevel());
        assertEquals(4, configuration.player().minNameGeneratedLength());
        assertEquals(8, configuration.player().maxNameGeneratedLength());
        assertEquals(1, configuration.player().spellBoostPointsOnLevelUp());
        assertEquals(5, configuration.player().characteristicPointsOnLevelUp());
        assertEquals(1000, configuration.player().baseLifeRegeneration());
    }

    @Test
    void activity() {
        assertEquals(1, configuration.activity().threadsCount());
        assertEquals(120, configuration.activity().monsterMoveInterval());
        assertEquals(25, configuration.activity().monsterMovePercent());
    }

    @Test
    void economy() {
        assertEquals(0.1, configuration.economy().npcSellPriceMultiplier());
        assertEquals(1, configuration.economy().bankCostPerEntry());
    }
}
