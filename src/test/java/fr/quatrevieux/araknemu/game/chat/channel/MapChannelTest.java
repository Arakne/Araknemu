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

package fr.quatrevieux.araknemu.game.chat.channel;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.chat.event.BroadcastedMessage;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;
import fr.quatrevieux.araknemu.network.game.out.info.Information;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MapChannelTest extends GameBaseCase {
    private MapChannel channel;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        channel = new MapChannel();
    }

    @Test
    void authorized() throws SQLException {
        assertFalse(channel.authorized(gamePlayer()));
        assertTrue(channel.authorized(explorationPlayer().player()));
    }

    @Test
    void sendExplorationMap() throws SQLException, ContainerException {
        ExplorationPlayer player = explorationPlayer();

        GameSession s2 = makeSimpleExplorationSession(5);
        GameSession s3 = makeSimpleExplorationSession(6);

        GamePlayer gp1 = gamePlayer();
        GamePlayer gp2 = s2.player();
        GamePlayer gp3 = s3.player();

        Listener<BroadcastedMessage> l1 = Mockito.mock(Listener.class);
        Listener<BroadcastedMessage> l2 = Mockito.mock(Listener.class);
        Listener<BroadcastedMessage> l3 = Mockito.mock(Listener.class);

        Mockito.when(l1.event()).thenReturn(BroadcastedMessage.class);
        Mockito.when(l2.event()).thenReturn(BroadcastedMessage.class);
        Mockito.when(l3.event()).thenReturn(BroadcastedMessage.class);

        gp1.dispatcher().add(l1);
        gp2.dispatcher().add(l2);
        gp3.dispatcher().add(l3);

        s2.exploration().changeMap(player.map(), 123);
        s3.exploration().changeMap(player.map(), 123);

        channel.send(
            gamePlayer(),
            new Message(ChannelType.MESSAGES, null, "hello", "")
        );

        Mockito.verify(l1).on(Mockito.any(BroadcastedMessage.class));
        Mockito.verify(l2).on(Mockito.any(BroadcastedMessage.class));
        Mockito.verify(l3).on(Mockito.any(BroadcastedMessage.class));
    }

    @Test
    void notOnMapShouldIgnore() throws SQLException, ContainerException {
        ExplorationPlayer player = explorationPlayer();
        player.leave();
        requestStack.clear();

        channel.send(gamePlayer(), new Message(ChannelType.MESSAGES, null, "hello", ""));

        requestStack.assertEmpty();
    }

    @Test
    void sendItem() throws SQLException, ContainerException {
        channel.send(
            gamePlayer(),
            new Message(ChannelType.MESSAGES, null, "hello °0", "2443!76#12#0#0#0d0+18,7e#1b#0#0#0d0+27")
        );

        requestStack.assertLast(Information.cannotPostItemOnChannel());
    }
}
