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
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.chat.ChatException;
import fr.quatrevieux.araknemu.game.chat.event.BroadcastedMessage;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.spectator.Spectator;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;
import fr.quatrevieux.araknemu.network.game.out.chat.MessageSent;
import fr.quatrevieux.araknemu.network.game.out.info.Information;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FightChannelTest extends FightBaseCase {
    private FightChannel channel;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        channel = new FightChannel();
    }

    @Test
    void sendItem() throws SQLException, ContainerException {
        channel.send(
            gamePlayer(),
            new Message(ChannelType.MESSAGES, null, "hello °0", "2443!76#12#0#0#0d0+18,7e#1b#0#0#0d0+27")
        );

        requestStack.assertLast(Information.cannotPostItemOnChannel());
    }

    @Test
    void authorized() throws Exception {
        assertFalse(channel.authorized(gamePlayer()));

        createFight();
        assertTrue(channel.authorized(gamePlayer()));
    }

    @Test
    void sendToFight() throws Exception {
        Fight fight = createFight();

        requestStack.clear();

        AtomicInteger count = new AtomicInteger();
        player.fighter().dispatcher().add(BroadcastedMessage.class, m -> count.incrementAndGet());
        other.fighter().dispatcher().add(BroadcastedMessage.class, m -> count.incrementAndGet());

        channel.send(player, new Message(ChannelType.MESSAGES, null, "hello", ""));

        requestStack.assertLast(new MessageSent(player, ChannelType.MESSAGES, "hello", ""));
        assertEquals(2, count.get());
    }

    @Test
    void sendToFightShouldAlsoSendToSpectators() throws Exception {
        Fight fight = createSimpleFight(container.get(ExplorationMapService.class).load(10340));

        Spectator spectator = new Spectator(gamePlayer(), fight);
        spectator.join();

        PlayerFighter fighter = PlayerFighter.class.cast(fight.fighters().get(0));
        GamePlayer from = fighter.player();
        from.start(fighter);

        channel.send(from, new Message(ChannelType.MESSAGES, null, "hello", ""));

        requestStack.assertLast(new MessageSent(from, ChannelType.MESSAGES, "hello", ""));
    }

    @Test
    void cannotSendItems() throws SQLException, ChatException {
        channel.send(
            gamePlayer(),
            new Message(ChannelType.MESSAGES, null, "hello °0", "2443!76#12#0#0#0d0+18,7e#1b#0#0#0d0+27")
        );

        requestStack.assertLast(Information.cannotPostItemOnChannel());
    }
}
