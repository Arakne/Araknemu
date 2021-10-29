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

package fr.quatrevieux.araknemu.game.fight.spectator;

import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.listener.fight.spectator.LeaveSpectatorOnDisconnect;
import fr.quatrevieux.araknemu.game.listener.fight.spectator.SendFightStateToSpectator;
import fr.quatrevieux.araknemu.game.listener.fight.spectator.SendSpectatorLeaveFight;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DefaultSpectatorFactoryTest extends FightBaseCase {
    private DefaultSpectatorFactory factory;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        factory = new DefaultSpectatorFactory();
    }

    @Test
    void create() throws SQLException {
        GamePlayer player = gamePlayer();
        Fight fight = createSimpleFight(container.get(ExplorationMapService.class).load(10340));

        Spectator spectator = factory.create(player, fight);

        assertSame(fight, spectator.fight());

        assertTrue(spectator.dispatcher().has(SendFightStateToSpectator.class));
        assertTrue(spectator.dispatcher().has(SendSpectatorLeaveFight.class));
        assertTrue(spectator.dispatcher().has(LeaveSpectatorOnDisconnect.class));
    }
}
