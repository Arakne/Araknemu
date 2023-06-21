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
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.chat.ChatException;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;
import fr.quatrevieux.araknemu.network.game.out.info.Information;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FloodGuardChannelTest extends GameBaseCase {
    private FloodGuardChannel channel;
    private Channel inner;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        channel = new FloodGuardChannel(
            inner = Mockito.mock(Channel.class),
            configuration.chat()
        );
    }

    @Test
    void authorized() throws SQLException, ContainerException {
        Mockito.when(inner.authorized(gamePlayer())).thenReturn(true);

        assertTrue(channel.authorized(gamePlayer()));
        Mockito.verify(inner).authorized(gamePlayer());
    }

    @Test
    void sendFirstTime() throws SQLException, ContainerException, ChatException {
        Message message = new Message(
            ChannelType.TRADE,
            null,
            "Hello",
            ""
        );

        channel.send(gamePlayer(), message);

        Mockito.verify(inner).send(gamePlayer(), message);
    }

    @Test
    void sendTwice() throws SQLException, ContainerException, ChatException {
        Message message = new Message(
            ChannelType.TRADE,
            null,
            "Hello",
            ""
        );

        channel.send(gamePlayer(), message);
        channel.send(gamePlayer(), message);

        Mockito.verify(inner, Mockito.atMost(1)).send(gamePlayer(), message);

        // Sending packet may take time, and the timer may be decremented
        String lastPacket = requestStack.channel.getMessages().peek().toString();

        assertTrue(
            lastPacket.equals(Information.chatFlood(30).toString())
            || lastPacket.equals(Information.chatFlood(29).toString())
            || lastPacket.equals(Information.chatFlood(28).toString())
        );
    }
}
