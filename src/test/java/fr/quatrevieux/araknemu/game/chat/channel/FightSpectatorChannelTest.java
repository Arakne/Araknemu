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

package fr.quatrevieux.araknemu.game.chat.channel;

import fr.quatrevieux.araknemu.core.network.util.DummyChannel;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.chat.ChatException;
import fr.quatrevieux.araknemu.game.chat.ChatService;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.spectator.Spectator;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;
import fr.quatrevieux.araknemu.network.game.out.chat.MessageSent;
import fr.quatrevieux.araknemu.network.game.out.info.Information;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FightSpectatorChannelTest extends FightBaseCase {
    private FightSpectatorChannel channel;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        channel = new FightSpectatorChannel(ChannelType.FIGHT_TEAM);
    }

    @Test
    void type() {
        assertSame(ChannelType.FIGHT_TEAM, channel.type());
        assertSame(ChannelType.MESSAGES, new FightSpectatorChannel(ChannelType.MESSAGES).type());
    }

    @Test
    void authorized() throws SQLException {
        assertFalse(channel.authorized(gamePlayer()));

        new Spectator(gamePlayer(), createSimpleFight(container.get(ExplorationMapService.class).load(10340))).join();
        assertTrue(channel.authorized(gamePlayer()));
    }

    @Test
    void sendShouldSendToSpectators() throws SQLException, NoSuchFieldException, IllegalAccessException, ChatException {
        Fight fight = createSimpleFight(container.get(ExplorationMapService.class).load(10340));

        Spectator spectator = new Spectator(gamePlayer(), fight);
        spectator.join();

        Field sf = GamePlayer.class.getDeclaredField("session");
        sf.setAccessible(true);
        GameSession otherSession = (GameSession) sf.get(other);
        Spectator otherSpectator = new Spectator(other, fight);
        otherSpectator.join();

        channel.send(gamePlayer(), new Message(ChannelType.MESSAGES, "", "Hello World !", ""));

        requestStack.assertLast(new MessageSent(
            gamePlayer(),
            ChannelType.FIGHT_TEAM,
            "Hello World !",
            ""
        ));

        new SendingRequestStack(DummyChannel.class.cast(otherSession.channel())).assertLast(new MessageSent(
            gamePlayer(),
            ChannelType.FIGHT_TEAM,
            "Hello World !",
            ""
        ));
    }

    @Test
    void sendShouldNotSendToFighters() throws Exception {
        Fight fight = createFight();

        GamePlayer sender = makeSimpleGamePlayer(10);
        Spectator spectator = new Spectator(sender, fight);
        spectator.join();
        requestStack.clear();

        channel.send(sender, new Message(ChannelType.MESSAGES, "", "Hello World !", ""));

        requestStack.assertEmpty();
    }

    @Test
    void cannotSendItems() throws SQLException, ChatException {
        channel.send(
            gamePlayer(),
            new Message(ChannelType.MESSAGES, null, "hello °0", "2443!76#12#0#0#0d0+18,7e#1b#0#0#0d0+27")
        );

        requestStack.assertLast(Information.cannotPostItemOnChannel());
    }

    @Test
    void sendFunctionalShouldSupportTeamAndDefaultChannelTypes() throws SQLException, ChatException {
        Fight fight = createSimpleFight(container.get(ExplorationMapService.class).load(10340));

        Spectator spectator = new Spectator(gamePlayer(), fight);
        spectator.join();

        container.get(ChatService.class).send(gamePlayer(), new Message(ChannelType.MESSAGES, "", "Hello World !", ""));
        requestStack.assertLast(new MessageSent(gamePlayer(), ChannelType.FIGHT_TEAM, "Hello World !", ""));

        container.get(ChatService.class).send(gamePlayer(), new Message(ChannelType.FIGHT_TEAM, "", "Hello World !", ""));
        requestStack.assertLast(new MessageSent(gamePlayer(), ChannelType.FIGHT_TEAM, "Hello World !", ""));
    }
}
