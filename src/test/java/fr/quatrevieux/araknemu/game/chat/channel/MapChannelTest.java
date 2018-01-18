package fr.quatrevieux.araknemu.game.chat.channel;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerRace;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.common.BroadcastedMessage;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.network.adapter.util.DummyChannel;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class MapChannelTest extends GameBaseCase {
    private MapChannel channel;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        channel = new MapChannel();
    }

    @Test
    void sendExplorationMap() throws SQLException, ContainerException {
        ExplorationPlayer player = explorationPlayer();

        GamePlayer gp1 = gamePlayer();
        GamePlayer gp2;
        GamePlayer gp3;

        ExplorationPlayer p2 = new ExplorationPlayer(
            gp2 = new GamePlayer(
                new GameAccount(
                    new Account(2),
                    container.get(AccountService.class),
                    1
                ),
                dataSet.createPlayer(2),
                new PlayerRace(Race.FECA),
                new GameSession(new DummyChannel()),
                container.get(PlayerService.class)
            )
        );

        ExplorationPlayer p3 = new ExplorationPlayer(
            gp3 = new GamePlayer(
                new GameAccount(
                    new Account(2),
                    container.get(AccountService.class),
                    1
                ),
                dataSet.createPlayer(3),
                new PlayerRace(Race.FECA),
                new GameSession(new DummyChannel()),
                container.get(PlayerService.class)
            )
        );

        Listener<BroadcastedMessage> l1 = Mockito.mock(Listener.class);
        Listener<BroadcastedMessage> l2 = Mockito.mock(Listener.class);
        Listener<BroadcastedMessage> l3 = Mockito.mock(Listener.class);

        Mockito.when(l1.event()).thenReturn(BroadcastedMessage.class);
        Mockito.when(l2.event()).thenReturn(BroadcastedMessage.class);
        Mockito.when(l3.event()).thenReturn(BroadcastedMessage.class);

        gp1.dispatcher().add(l1);
        gp2.dispatcher().add(l2);
        gp3.dispatcher().add(l3);

        player.map().add(p2);
        player.map().add(p3);

        channel.send(
            gamePlayer(),
            new Message(ChannelType.MESSAGES, null, "hello", "")
        );

        Mockito.verify(l1).on(Mockito.any(BroadcastedMessage.class));
        Mockito.verify(l2).on(Mockito.any(BroadcastedMessage.class));
        Mockito.verify(l3).on(Mockito.any(BroadcastedMessage.class));
    }
}
