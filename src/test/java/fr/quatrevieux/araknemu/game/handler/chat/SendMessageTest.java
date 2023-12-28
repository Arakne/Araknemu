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

package fr.quatrevieux.araknemu.game.handler.chat;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.core.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.core.network.util.DummyChannel;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.chat.ChatException;
import fr.quatrevieux.araknemu.game.chat.ChatService;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.spectator.Spectator;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.Restrictions;
import fr.quatrevieux.araknemu.game.player.event.PlayerLoaded;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;
import fr.quatrevieux.araknemu.network.game.out.basic.Noop;
import fr.quatrevieux.araknemu.network.game.out.chat.MessageSent;
import fr.quatrevieux.araknemu.network.game.out.chat.SendMessageError;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class SendMessageTest extends FightBaseCase {
    private SendMessage handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new SendMessage(
            container.get(ChatService.class)
        );

        container.get(Dispatcher.class).dispatch(
            new PlayerLoaded(gamePlayer())
        );
    }

    @Test
    void handleMapMessageOnExploration() throws Exception {
        explorationPlayer();

        handler.handle(
            session,
            new Message(
                ChannelType.MESSAGES,
                null,
                "Hello World !",
                ""
            )
        );

        requestStack.assertLast(
            new MessageSent(
                gamePlayer(),
                ChannelType.MESSAGES,
                "Hello World !",
                ""
            )
        );
    }

    @Test
    void handleUnauthorizedChannel() throws Exception {
        explorationPlayer();

        try {
            handler.handle(
                session,
                new Message(
                    ChannelType.ADMIN,
                    null,
                    "Hello World !",
                    ""
                )
            );

            fail("Error packet should be thrown");
        } catch (ErrorPacket packet) {
            assertEquals(Error.cantDoOnServer().toString(), packet.packet().toString());
        }
    }

    @Test
    void handleInvalidPrivateTarget() throws Exception {
        explorationPlayer();

        try {
            handler.handle(
                session,
                new Message(
                    ChannelType.PRIVATE,
                    "not found",
                    "Hello World !",
                    ""
                )
            );

            fail("Error packet should be thrown");
        } catch (ErrorPacket packet) {
            assertEquals(
                new SendMessageError(ChatException.Error.USER_NOT_CONNECTED, "not found").toString(),
                packet.packet().toString()
            );
        }
    }

    @Test
    void handleSyntaxError() throws Exception {
        explorationPlayer();

        try {
            handler.handle(
                session,
                new Message(
                    ChannelType.TRADE,
                    "",
                    "°0",
                    "invalid"
                )
            );

            fail("Error packet should be thrown");
        } catch (ErrorPacket packet) {
            assertEquals(new SendMessageError(ChatException.Error.SYNTAX_ERROR, "").toString(), packet.packet().toString());
        }
    }

    @Test
    void handleCantChat() throws Exception {
        explorationPlayer();
        gamePlayer().restrictions().set(Restrictions.Restriction.DENY_CHAT);

        try {
            handler.handle(
                session,
                new Message(ChannelType.MESSAGES, null, "Hello World !", "")
            );

            fail("Error packet should be thrown");
        } catch (ErrorPacket packet) {
            assertEquals(Error.cantDoOnCurrentState().toString(), packet.packet().toString());
        }
    }

    @Test
    void asSpectatorFromGlobalChannel() throws Exception {
        Fight fight = createSimpleFight(container.get(ExplorationMapService.class).load(10340));

        Spectator spectator = new Spectator(gamePlayer(), fight);
        spectator.join();

        Field sf = GamePlayer.class.getDeclaredField("session");
        sf.setAccessible(true);
        GameSession otherSession = (GameSession) sf.get(other);
        Spectator otherSpectator = new Spectator(other, fight);
        otherSpectator.join();

        handlePacket(new Message(ChannelType.MESSAGES, "", "Hello World !", ""));

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
    void handleEmptyMessage() throws Exception {
        explorationPlayer();
        requestStack.clear();

        session.receive("BM*||");
        requestStack.assertLast("BN");
        requestStack.clear();

        session.receive("BM*||\t\n   \t ");
        requestStack.assertLast("BN");
        requestStack.clear();
    }
}
