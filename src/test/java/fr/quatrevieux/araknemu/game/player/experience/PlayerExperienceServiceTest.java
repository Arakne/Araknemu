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

package fr.quatrevieux.araknemu.game.player.experience;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerExperience;
import fr.quatrevieux.araknemu.data.world.repository.character.PlayerExperienceRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.listener.player.RebuildLifePointsOnLevelUp;
import fr.quatrevieux.araknemu.game.listener.player.SendLevelUp;
import fr.quatrevieux.araknemu.game.listener.player.SendPlayerXp;
import fr.quatrevieux.araknemu.game.player.event.PlayerLoaded;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class PlayerExperienceServiceTest extends GameBaseCase {
    private PlayerExperienceService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = new PlayerExperienceService(
            container.get(PlayerExperienceRepository.class),
            container.get(GameConfiguration.class).player()
        );

        dataSet.pushExperience();

        service.init(container.get(Logger.class));
    }

    @Test
    void preload() {
        Logger logger = Mockito.mock(Logger.class);

        service.init(logger);

        Mockito.verify(logger).info("Loading player experience...");
        Mockito.verify(logger).info("{} player levels loaded", 200);
    }

    @Test
    void playerLoadlistener() throws SQLException, ContainerException {
        ListenerAggregate dispatcher = new DefaultListenerAggregate();
        dispatcher.register(service);

        dispatcher.dispatch(new PlayerLoaded(gamePlayer()));

        assertTrue(gamePlayer().dispatcher().has(SendPlayerXp.class));
        assertTrue(gamePlayer().dispatcher().has(SendLevelUp.class));
        assertTrue(gamePlayer().dispatcher().has(RebuildLifePointsOnLevelUp.class)); // Issue #55
    }

    @Test
    void load() throws ContainerException {
        Player player = dataSet.pushPlayer("a", 1, 2);
        player.setLevel(25);
        player.setExperience(375698);

        GamePlayerExperience level = service.load(new DefaultListenerAggregate(), player);

        assertEquals(25, level.level());
        assertEquals(375698, level.current());
        assertEquals(353000, level.min());
        assertEquals(398500, level.max());
        assertFalse(level.maxLevelReached());
    }

    @Test
    void maxLevel() {
        assertEquals(200, service.maxLevel());
    }

    @Test
    void byLevel() {
        PlayerExperience experience = service.byLevel(125);

        assertEquals(125, experience.level());
        assertEquals(240452000, experience.experience());
    }

    @Test
    void byLevelHigherThanMax() {
        PlayerExperience experience = service.byLevel(300);

        assertEquals(200, experience.level());
        assertEquals(7407232000L, experience.experience());
    }

    @Test
    void applyNewLevelBonus() {
        Player player = new Player(1);
        player.setLevel(5);
        player.setBoostPoints(5);
        player.setSpellPoints(2);

        service.applyLevelUpBonus(player, 10);

        assertEquals(30, player.boostPoints());
        assertEquals(7, player.spellPoints());
    }
}
