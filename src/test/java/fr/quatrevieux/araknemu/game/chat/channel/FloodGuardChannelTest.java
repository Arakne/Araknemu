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

import static org.junit.jupiter.api.Assertions.*;

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

        requestStack.assertLast(Information.chatFlood(30));
    }
}
