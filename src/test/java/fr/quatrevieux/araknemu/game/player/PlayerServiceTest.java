package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.repository.character.PlayerRaceRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.event.common.Disconnected;
import fr.quatrevieux.araknemu.game.event.common.PlayerLoaded;
import fr.quatrevieux.araknemu.game.event.listener.player.SavePlayer;
import fr.quatrevieux.araknemu.network.adapter.util.DummyChannel;
import fr.quatrevieux.araknemu.network.game.GameSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class PlayerServiceTest extends GameBaseCase {
    private PlayerService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = new PlayerService(
            container.get(PlayerRepository.class),
            container.get(PlayerRaceRepository.class),
            container.get(GameConfiguration.class),
            container.get(Dispatcher.class)
        );

        login();
        dataSet.pushRaces();
    }

    @Test
    void loadInvalidServer() throws ContainerException, SQLException {
        int id = dataSet.push(new Player(-1, 1, 3, "Bob", Race.FECA, Sex.MALE, new Colors(123, 456, 789), 23, null)).id();

        assertThrows(EntityNotFoundException.class, () -> service.load(session, id));
    }

    @Test
    void loadSuccess() throws ContainerException, SQLException {
        int id = dataSet.push(new Player(-1, 1, 2, "Bob", Race.FECA, Sex.MALE, new Colors(123, 456, 789), 23, null)).id();

        GamePlayer player = service.load(session, id);

        assertEquals(id, player.id());

        assertTrue(player.dispatcher().has(SavePlayer.class));
    }

    @Test
    void loadWillDispatchPlayerLoaded() throws ContainerException {
        AtomicReference<PlayerLoaded> ref = new AtomicReference<>();

        Listener<PlayerLoaded> listener = new Listener<PlayerLoaded>() {
            @Override
            public void on(PlayerLoaded event) {
                ref.set(event);
            }

            @Override
            public Class<PlayerLoaded> event() {
                return PlayerLoaded.class;
            }
        };

        container.get(ListenerAggregate.class).add(listener);

        int id = dataSet.push(new Player(-1, 1, 2, "Bob", Race.FECA, Sex.MALE, new Colors(123, 456, 789), 23, null)).id();

        GamePlayer player = service.load(session, id);

        assertSame(player, ref.get().player());
    }

    @Test
    void loadWillAddToOnlinePlayers() throws ContainerException, SQLException {
        int id = dataSet.push(new Player(-1, 1, 2, "Bob", Race.FECA, Sex.MALE, new Colors(123, 456, 789), 23, null)).id();

        GamePlayer player = service.load(session, id);

        assertTrue(
            service.online().contains(player)
        );
    }

    @Test
    void loadOnlinePlayerWillThrowException() throws ContainerException {
        int id = dataSet.push(new Player(-1, 1, 2, "Bob", Race.FECA, Sex.MALE, new Colors(123, 456, 789), 23, null)).id();

        service.load(session, id);

        assertThrows(IllegalStateException.class, () -> service.load(session, id));
    }

    @Test
    void loadedPlayerOnDisconnectWillBeRemovedFromOnlineList() throws ContainerException {
        int id = dataSet.push(new Player(-1, 1, 2, "Bob", Race.FECA, Sex.MALE, new Colors(123, 456, 789), 23, null)).id();

        GamePlayer player = service.load(session, id);

        player.dispatch(new Disconnected());

        assertFalse(service.online().contains(player));
    }

    @Test
    void filter() throws ContainerException {
        GameSession session1 = new GameSession(new DummyChannel());
        session1.attach(new GameAccount(new Account(1), container.get(AccountService.class), 2));
        service.load(session1, dataSet.pushPlayer("Bob", 1, 2).id());

        GameSession session2 = new GameSession(new DummyChannel());
        session2.attach(new GameAccount(new Account(2), container.get(AccountService.class), 2));
        service.load(session2, dataSet.pushPlayer("Robert", 2, 2).id());

        GameSession session3 = new GameSession(new DummyChannel());
        session3.attach(new GameAccount(new Account(3), container.get(AccountService.class), 2));
        service.load(session3, dataSet.pushPlayer("Jean", 3, 2).id());

        GameSession session4 = new GameSession(new DummyChannel());
        session4.attach(new GameAccount(new Account(4), container.get(AccountService.class), 2));
        service.load(session4, dataSet.pushPlayer("Kevin", 4, 2).id());

        GameSession session5 = new GameSession(new DummyChannel());
        session5.attach(new GameAccount(new Account(5), container.get(AccountService.class), 2));
        service.load(session5, dataSet.pushPlayer("Louis", 5, 2).id());

        assertEquals(3, service.filter(player -> player.name().contains("o")).count());
        assertEquals(1, service.filter(player -> player.name().equalsIgnoreCase("bob")).count());
        assertEquals(0, service.filter(player -> player.name().equalsIgnoreCase("no exists")).count());
    }

    @Test
    void isOnline() throws ContainerException {
        assertFalse(service.isOnline("no_found"));

        GameSession session1 = new GameSession(new DummyChannel());
        session1.attach(new GameAccount(new Account(1), container.get(AccountService.class), 2));
        GamePlayer player = service.load(session1, dataSet.pushPlayer("Bob", 1, 2).id());

        assertTrue(service.isOnline("bob"));

        player.dispatch(new Disconnected());

        assertFalse(service.isOnline("bob"));
    }

    @Test
    void getNotFound() {
        assertThrows(NoSuchElementException.class, () -> service.get("not_found"));
    }

    @Test
    void getSuccess() throws ContainerException {
        GameSession session1 = new GameSession(new DummyChannel());
        session1.attach(new GameAccount(new Account(1), container.get(AccountService.class), 2));
        GamePlayer player = service.load(session1, dataSet.pushPlayer("Bob", 1, 2).id());

        assertSame(player, service.get("bob"));
    }

    @Test
    void save() throws ContainerException {
        Player entity = dataSet.pushPlayer("Bob", 1, 2);
        GamePlayer player = service.load(session, entity.id());

        player.setPosition(new Position(963, 258));

        service.save(player);

        assertEquals(new Position(963, 258), dataSet.refresh(entity).position());
    }
}
