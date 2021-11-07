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
import fr.quatrevieux.araknemu.game.chat.event.ConcealedMessage;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrivateChannelTest extends GameBaseCase {
    private PrivateChannel channel;
    private GamePlayer other;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushRaces();
        gamePlayer();

        channel = new PrivateChannel(
            container.get(PlayerService.class)
        );

        other = makeOtherPlayer();
    }

    @Test
    void authorized() throws SQLException {
        assertTrue(channel.authorized(gamePlayer()));
    }

    @Test
    void notOnline() throws SQLException, ContainerException {
        assertThrows(ChatException.class, () -> channel.send(
            gamePlayer(),
            new Message(
                ChannelType.PRIVATE,
                "Not found",
                "",
                ""
            )
        ));
    }

    @Test
    void success() throws SQLException, ContainerException, ChatException {
        List<ConcealedMessage> events = new ArrayList<>();

        gamePlayer().dispatcher().add(ConcealedMessage.class, events::add);
        other.dispatcher().add(ConcealedMessage.class, events::add);

        channel.send(
            gamePlayer(),
            new Message(
                ChannelType.PRIVATE,
                other.name(),
                "Hello",
                ""
            )
        );

        assertCount(2, events);
        assertEquals("Hello", events.get(0).message());
        assertEquals(gamePlayer(), events.get(0).sender());
        assertEquals(other, events.get(0).receiver());
    }
}