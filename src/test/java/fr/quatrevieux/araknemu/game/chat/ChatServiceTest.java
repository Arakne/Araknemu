package fr.quatrevieux.araknemu.game.chat;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.chat.channel.Channel;
import fr.quatrevieux.araknemu.game.chat.channel.GlobalChannel;
import fr.quatrevieux.araknemu.game.chat.channel.MapChannel;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.listener.player.chat.InitializeChat;
import fr.quatrevieux.araknemu.game.listener.player.chat.MessageReceived;
import fr.quatrevieux.araknemu.game.listener.player.chat.AddChatChannels;
import fr.quatrevieux.araknemu.game.listener.player.chat.PrivateMessageReceived;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.game.player.event.PlayerLoaded;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;
import fr.quatrevieux.araknemu.network.game.out.chat.MessageSent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ChatServiceTest extends GameBaseCase {
    private ChatService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = new ChatService(
            container.get(GameConfiguration.class).chat(),
            new Channel[] {
                new MapChannel(),
                new GlobalChannel(ChannelType.TRADE, container.get(PlayerService.class))
            }
        );
    }

    @Test
    void listeners() {
        ListenerAggregate dispatcher = new DefaultListenerAggregate();
        dispatcher.register(service);

        assertTrue(dispatcher.has(AddChatChannels.class));
    }

    @Test
    void playerLoadListener() throws SQLException, ContainerException {
        ListenerAggregate dispatcher = new DefaultListenerAggregate();
        dispatcher.register(service);

        dispatcher.dispatch(new PlayerLoaded(gamePlayer()));

        assertTrue(gamePlayer().dispatcher().has(InitializeChat.class));
        assertTrue(gamePlayer().dispatcher().has(MessageReceived.class));
        assertTrue(gamePlayer().dispatcher().has(PrivateMessageReceived.class));
    }

    @Test
    void sendMapChat() throws SQLException, ContainerException, ChatException {
        explorationPlayer();
        gamePlayer().dispatcher().add(new MessageReceived(
            gamePlayer()
        ));

        service.send(
            gamePlayer(),
            new Message(ChannelType.MESSAGES, "", "My message", "")
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

    @Test
    void sendSyntaxError() throws SQLException, ContainerException, ChatException {
        explorationPlayer();

        assertThrows(ChatException.class, () -> service.send(gamePlayer(), new Message(ChannelType.MESSAGES, "", "°0", "1234")));
        assertThrows(ChatException.class, () -> service.send(gamePlayer(), new Message(ChannelType.MESSAGES, "", "message", "2443!76#12#0#0#0d0+18,7e#1b#0#0#0d0+27")));
        assertThrows(ChatException.class, () -> service.send(gamePlayer(), new Message(ChannelType.MESSAGES, "", "°0", "aaa!zzz")));
        assertThrows(ChatException.class, () -> service.send(gamePlayer(), new Message(ChannelType.MESSAGES, "", "°0", "123!45#10")));
    }

    @Test
    void sendWithItemSuccess() throws SQLException, ContainerException, ChatException {
        gamePlayer(true).dispatcher().add(new MessageReceived(
            gamePlayer()
        ));
        service.send(gamePlayer(), new Message(ChannelType.TRADE, "", "Hello °0", "2443!76#12#0#0#0d0+18,7e#1b#0#0#0d0+27"));

        requestStack.assertLast(
            new MessageSent(
                gamePlayer(),
                ChannelType.TRADE,
                "Hello °0",
                "2443!76#12#0#0#0d0+18,7e#1b#0#0#0d0+27"
            )
        );
    }
}
