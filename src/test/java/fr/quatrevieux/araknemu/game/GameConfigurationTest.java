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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameConfigurationTest extends GameBaseCase {
    private GameConfiguration configuration;

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        configuration = app.configuration().module(GameConfiguration.MODULE);
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
    void preload() {
        assertTrue(configuration.preload("bar"));
        assertTrue(configuration.preload("bar.baz"));
        assertFalse(configuration.preload("foo"));
        assertFalse(configuration.preload("foo.bar"));
        assertFalse(configuration.preload("foo.bar.baz"));
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
        assertTrue(configuration.player().restoreLifeOnLevelUp());
    }

    @Test
    void activity() {
        assertEquals(1, configuration.activity().threadsCount());
        assertEquals(120, configuration.activity().monsterMoveInterval());
        assertEquals(25, configuration.activity().monsterMovePercent());
        assertEquals(1, configuration.activity().monsterRespawnSpeedFactor());
        setConfigValue("activity.monsters.respawnSpeedFactor", "3");
        assertEquals(5, configuration.activity().monsterMoveDistance());
        setConfigValue("activity.monsters.moveDistance", "3");
        assertEquals(3, configuration.activity().monsterMoveDistance());
    }

    @Test
    void economy() {
        assertEquals(0.1, configuration.economy().npcSellPriceMultiplier());
        assertEquals(1, configuration.economy().bankCostPerEntry());
    }

    @Test
    void fight() {
        assertEquals(4, configuration.fight().threadsCount());
        setConfigValue("fight.threadsCount", "8");
        assertEquals(8, configuration.fight().threadsCount());

        assertEquals(Duration.ofSeconds(30), configuration.fight().turnDuration());
        setConfigValue("fight.turnDuration", "1m30s");
        assertEquals(Duration.ofSeconds(90), configuration.fight().turnDuration());

        assertEquals(Duration.ofSeconds(45), configuration.fight().pvmPlacementDuration());
        setConfigValue("fight.pvm.placementDuration", "1m30s");
        assertEquals(Duration.ofSeconds(90), configuration.fight().pvmPlacementDuration());

        assertEquals(1.0, configuration.fight().xpRate());
        assertEquals(1.0, configuration.fight().dropRate());
    }

    @Test
    void autosave() {
        assertTrue(configuration.autosaveEnabled());
        assertEquals(Duration.ofHours(4), configuration.autosaveInterval());

        setConfigValue("autosave.enabled", "no");
        setConfigValue("autosave.interval", "30s");

        assertFalse(configuration.autosaveEnabled());
        assertEquals(Duration.ofSeconds(30), configuration.autosaveInterval());
    }

    @Test
    void timezone() {
        assertEquals("Europe/Paris", configuration.timezone().getId());

        setConfigValue("server.timezone", "America/Bogota");

        assertEquals("America/Bogota", configuration.timezone().getId());
    }
}
