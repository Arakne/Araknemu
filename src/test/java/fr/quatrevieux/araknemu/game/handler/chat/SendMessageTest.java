package fr.quatrevieux.araknemu.game.handler.chat;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.chat.ChatException;
import fr.quatrevieux.araknemu.game.chat.ChatService;
import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.event.common.PlayerLoaded;
import fr.quatrevieux.araknemu.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;
import fr.quatrevieux.araknemu.network.game.out.chat.MessageSent;
import fr.quatrevieux.araknemu.network.game.out.chat.SendMessageError;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.helpers.NOPLogger;

import static org.junit.jupiter.api.Assertions.*;

class SendMessageTest extends GameBaseCase {
    private SendMessage handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new SendMessage(
            container.get(ChatService.class)
        );

        container.get(ChatService.class).preload(NOPLogger.NOP_LOGGER);
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
}
