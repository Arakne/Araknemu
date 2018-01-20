package fr.quatrevieux.araknemu.game.chat;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.chat.channel.Channel;
import fr.quatrevieux.araknemu.game.chat.channel.MapChannel;
import fr.quatrevieux.araknemu.game.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.event.listener.player.chat.MessageReceived;
import fr.quatrevieux.araknemu.game.event.listener.service.AddChatChannels;
import fr.quatrevieux.araknemu.game.event.listener.service.RegisterChatListeners;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;
import fr.quatrevieux.araknemu.network.game.out.chat.MessageSent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.helpers.NOPLogger;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ChatServiceTest extends GameBaseCase {
    private ChatService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = new ChatService(
            container.get(ListenerAggregate.class),
            container.get(GameConfiguration.class).chat(),
            new Channel[] {
                new MapChannel()
            }
        );
    }

    @Test
    void preload() throws ContainerException {
        service.preload(NOPLogger.NOP_LOGGER);

        assertTrue(container.get(ListenerAggregate.class).has(RegisterChatListeners.class));
        assertTrue(container.get(ListenerAggregate.class).has(AddChatChannels.class));
    }

    @Test
    void sendMapChat() throws SQLException, ContainerException, ChatException {
        explorationPlayer();
        gamePlayer().dispatcher().add(new MessageReceived(
            gamePlayer()
        ));

        service.send(
            gamePlayer(),
            new Message(ChannelType.MESSAGES, null, "My message", "")
        );

        requestStack.assertLast(
            new MessageSent(
                gamePlayer(),
                ChannelType.MESSAGES,
                "My message",
                ""
            )
        );
    }
}
