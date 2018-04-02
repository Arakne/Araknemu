package fr.quatrevieux.araknemu.game.listener.player.chat;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.chat.event.BroadcastedMessage;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.chat.MessageSent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class MessageReceivedTest extends GameBaseCase {
    private MessageReceived listener;
    private GamePlayer other;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        other = makeOtherPlayer();

        listener = new MessageReceived(
            gamePlayer()
        );
    }

    @Test
    void onBroadcastedMessage() throws SQLException, ContainerException {
        listener.on(
            new BroadcastedMessage(
                ChannelType.INFO,
                gamePlayer(),
                "Hello World !",
                ""
            )
        );

        requestStack.assertLast(
            new MessageSent(
                gamePlayer(),
                ChannelType.INFO,
                "Hello World !",
                ""
            )
        );
    }

    @Test
    void channelNotSubscribedFromMe() throws SQLException, ContainerException {
        gamePlayer().subscriptions().remove(ChannelType.MESSAGES);

        listener.on(
            new BroadcastedMessage(
                ChannelType.MESSAGES,
                gamePlayer(),
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
    void channelNotSubscribedFromOther() throws SQLException, ContainerException {
        gamePlayer().subscriptions().remove(ChannelType.MESSAGES);

        listener.on(
            new BroadcastedMessage(
                ChannelType.MESSAGES,
                other,
                "Hello World !",
                ""
            )
        );

        requestStack.assertEmpty();
    }
}
