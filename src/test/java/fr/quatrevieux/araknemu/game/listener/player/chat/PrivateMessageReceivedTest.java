package fr.quatrevieux.araknemu.game.listener.player.chat;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.chat.event.ConcealedMessage;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.chat.PrivateMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class PrivateMessageReceivedTest extends GameBaseCase {
    private PrivateMessageReceived listener;
    private GamePlayer other;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushRaces();

        listener = new PrivateMessageReceived(
            gamePlayer()
        );

        other = makeOtherPlayer();
    }

    @Test
    void onSendMessage() throws SQLException, ContainerException {
        listener.on(
            new ConcealedMessage(
                gamePlayer(),
                other,
                "hello",
                ""
            )
        );

        requestStack.assertLast(
            new PrivateMessage(
                PrivateMessage.TYPE_TO,
                other,
                "hello",
                ""
            )
        );
    }

    @Test
    void onReceiveMessage() throws SQLException, ContainerException {
        listener.on(
            new ConcealedMessage(
                other,
                gamePlayer(),
                "hello",
                ""
            )
        );

        requestStack.assertLast(
            new PrivateMessage(
                PrivateMessage.TYPE_FROM,
                other,
                "hello",
                ""
            )
        );
    }
}
