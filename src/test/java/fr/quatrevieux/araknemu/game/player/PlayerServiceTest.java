/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.player;

import fr.arakne.utils.value.Colors;
import fr.arakne.utils.value.constant.Gender;
import fr.arakne.utils.value.constant.Race;
import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.core.network.session.SessionFactory;
import fr.quatrevieux.araknemu.core.network.util.DummyChannel;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.event.ShutdownScheduled;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.handler.event.Disconnected;
import fr.quatrevieux.araknemu.game.listener.player.*;
import fr.quatrevieux.araknemu.game.player.event.PlayerLoaded;
import fr.quatrevieux.araknemu.game.player.experience.PlayerExperienceService;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryService;
import fr.quatrevieux.araknemu.game.player.race.PlayerRaceService;
import fr.quatrevieux.araknemu.game.player.spell.SpellBookService;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.time.Duration;
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
            container.get(GameConfiguration.class),
            container.get(Dispatcher.class),
            container.get(InventoryService.class),
            container.get(PlayerRaceService.class),
            container.get(SpellBookService.class),
            container.get(PlayerExperienceService.class)
        );

        login();
        dataSet.use(PlayerItem.class);
        dataSet
            .pushRaces()
            .pushSpells()
            .use(PlayerSpell.class)
        ;
    }

    @Test
    void loadInvalidServer() throws ContainerException, SQLException {
        int id = dataSet.push(new Player(-1, 1, 3, "Bob", Race.FECA, Gender.MALE, new Colors(123, 456, 789), 23, null)).id();

        assertThrows(EntityNotFoundException.class, () -> service.load(session, id));
    }

    @Test
    void loadSuccess() throws ContainerException {
        int id = dataSet.push(new Player(-1, 1, 2, "Bob", Race.FECA, Gender.MALE, new Colors(123, 456, 789), 23, null)).id();

        GamePlayer player = service.load(session, id);

        assertEquals(id, player.id());

        assertTrue(player.dispatcher().has(SavePlayer.class));
        assertTrue(player.dispatcher().has(SendStats.class));
        assertTrue(player.dispatcher().has(ComputeLifePoints.class));
        assertTrue(player.dispatcher().has(SendLifeChanged.class));
        assertTrue(player.dispatcher().has(SendRestrictions.class));
        assertTrue(player.dispatcher().has(InitializeRestrictions.class));
        assertTrue(player.dispatcher().has(StartTutorial.class));
        assertTrue(player.dispatcher().has(RestoreLifePointsOnLevelUp.class));
    }

    @Test
    void loadWithRestoreLifeOnLevelUpDisabled() throws ContainerException {
        setConfigValue("player.restoreLifeOnLevelUp", "false");
        int id = dataSet.push(new Player(-1, 1, 2, "Bob", Race.FECA, Gender.MALE, new Colors(123, 456, 789), 23, null)).id();

        GamePlayer player = service.load(session, id);

        assertEquals(id, player.id());

        assertFalse(player.dispatcher().has(RestoreLifePointsOnLevelUp.class));
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

        int id = dataSet.push(new Player(-1, 1, 2, "Bob", Race.FECA, Gender.MALE, new Colors(123, 456, 789), 23, null)).id();

        GamePlayer player = service.load(session, id);

        assertSame(player, ref.get().player());
    }

    @Test
    void loadWillAddToOnlinePlayers() throws ContainerException, SQLException {
        int id = dataSet.push(new Player(-1, 1, 2, "Bob", Race.FECA, Gender.MALE, new Colors(123, 456, 789), 23, null)).id();

        GamePlayer player = service.load(session, id);

        assertTrue(
            service.online().contains(player)
        );
    }

    @Test
    void loadOnlinePlayerWillThrowException() throws ContainerException {
        int id = dataSet.push(new Player(-1, 1, 2, "Bob", Race.FECA, Gender.MALE, new Colors(123, 456, 789), 23, null)).id();

        service.load(session, id);

        assertThrows(IllegalStateException.class, () -> service.load(session, id));
    }

    @Test
    void loadedPlayerOnDisconnectWillBeRemovedFromOnlineList() throws ContainerException {
        int id = dataSet.push(new Player(-1, 1, 2, "Bob", Race.FECA, Gender.MALE, new Colors(123, 456, 789), 23, null)).id();

        GamePlayer player = service.load(session, id);
        session.setPlayer(player);

        player.dispatch(new Disconnected());

        assertFalse(service.online().contains(player));
    }

    @Test
    void filter() throws ContainerException {
        GameSession session1 = (GameSession) container.get(SessionFactory.class).create(new DummyChannel());
        session1.attach(new GameAccount(new Account(1), container.get(AccountService.class), 2));
        service.load(session1, dataSet.pushPlayer("Bob", 1, 2).id());

        GameSession session2 = (GameSession) container.get(SessionFactory.class).create(new DummyChannel());
        session2.attach(new GameAccount(new Account(2), container.get(AccountService.class), 2));
        service.load(session2, dataSet.pushPlayer("Robert", 2, 2).id());

        GameSession session3 = (GameSession) container.get(SessionFactory.class).create(new DummyChannel());
        session3.attach(new GameAccount(new Account(3), container.get(AccountService.class), 2));
        service.load(session3, dataSet.pushPlayer("Jean", 3, 2).id());

        GameSession session4 = (GameSession) container.get(SessionFactory.class).create(new DummyChannel());
        session4.attach(new GameAccount(new Account(4), container.get(AccountService.class), 2));
        service.load(session4, dataSet.pushPlayer("Kevin", 4, 2).id());

        GameSession session5 = (GameSession) container.get(SessionFactory.class).create(new DummyChannel());
        session5.attach(new GameAccount(new Account(5), container.get(AccountService.class), 2));
        service.load(session5, dataSet.pushPlayer("Louis", 5, 2).id());

        assertEquals(3, service.filter(player -> player.name().contains("o")).count());
        assertEquals(1, service.filter(player -> player.name().equalsIgnoreCase("bob")).count());
        assertEquals(0, service.filter(player -> player.name().equalsIgnoreCase("no exists")).count());
    }

    @Test
    void isOnline() throws ContainerException {
        assertFalse(service.isOnline("no_found"));

        GameSession session1 = (GameSession) container.get(SessionFactory.class).create(new DummyChannel());
        session1.attach(new GameAccount(new Account(1), container.get(AccountService.class), 2));
        GamePlayer player = service.load(session1, dataSet.pushPlayer("Bob", 1, 2).id());
        session1.setPlayer(player);

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
        GameSession session1 = (GameSession) container.get(SessionFactory.class).create(new DummyChannel());
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

    @Test
    void shutdownScheduled() {
        int id = dataSet.push(new Player(-1, 1, 2, "Bob", Race.FECA, Gender.MALE, new Colors(123, 456, 789), 23, null)).id();
        service.load(session, id);

        ListenerAggregate dispatcher = new DefaultListenerAggregate();
        dispatcher.register(service);

        dispatcher.dispatch(new ShutdownScheduled(Duration.ofMinutes(10)));
        requestStack.assertLast(Error.shutdownScheduled("10min"));
    }

    @Test
    void send() throws Exception {
        GamePlayer other = makeOtherPlayer();
        gamePlayer(true);

        Field field = other.getClass().getDeclaredField("session");
        field.setAccessible(true);

        GameSession otherSession = (GameSession) field.get(other);
        SendingRequestStack otherRequestStack = new SendingRequestStack((DummyChannel) otherSession.channel());

        container.get(PlayerService.class).send("my packet");

        requestStack.assertLast("my packet");
        otherRequestStack.assertLast("my packet");
    }
}
