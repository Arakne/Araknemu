package fr.quatrevieux.araknemu.game.event.listener.player.chat;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.world.repository.character.PlayerRaceRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.event.common.ConcealedMessage;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.adapter.util.DummyChannel;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.out.chat.PrivateMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

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

        other = new GamePlayer(
            new GameAccount(
                new Account(3),
                container.get(AccountService.class),
                2
            ),
            dataSet.createPlayer(2),
            container.get(PlayerRaceRepository.class).get(Race.FECA),
            new GameSession(new DummyChannel())
        );
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
