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

package fr.quatrevieux.araknemu.game.fight.fighter;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.event.PlayerFighterCreated;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.listener.fight.SendFightJoined;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.*;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class DefaultFighterFactoryTest extends GameBaseCase {
    private DefaultFighterFactory factory;
    private ListenerAggregate dispatcher;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        factory = new DefaultFighterFactory(
            dispatcher = new DefaultListenerAggregate()
        );
    }

    @Test
    void createPlayerFighter() throws SQLException, ContainerException {
        AtomicReference<PlayerFighterCreated> ref = new AtomicReference<>();
        dispatcher.add(PlayerFighterCreated.class, ref::set);

        GamePlayer player = gamePlayer();

        PlayerFighter fighter = factory.create(player);

        assertSame(fighter, ref.get().fighter());
        assertSame(player, fighter.player());

        assertTrue(fighter.dispatcher().has(SendFightJoined.class));
        assertTrue(fighter.dispatcher().has(ApplyEndFightReward.class));
        assertTrue(fighter.dispatcher().has(StopFightSession.class));
        assertTrue(fighter.dispatcher().has(SendFightLeaved.class));
        assertTrue(fighter.dispatcher().has(LeaveOnDisconnect.class));
        assertTrue(fighter.dispatcher().has(ApplyLeaveReward.class));
        assertTrue(fighter.dispatcher().has(SendStats.class));
        assertTrue(fighter.dispatcher().has(SendSpellBoosted.class));
    }
}
