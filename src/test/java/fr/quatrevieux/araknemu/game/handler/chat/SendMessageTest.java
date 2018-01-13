package fr.quatrevieux.araknemu.game.handler.chat;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.chat.ChatService;
import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.event.common.PlayerLoaded;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;
import fr.quatrevieux.araknemu.network.game.out.chat.MessageSent;
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
}
