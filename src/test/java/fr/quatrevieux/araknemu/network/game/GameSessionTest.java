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

package fr.quatrevieux.araknemu.network.game;

import fr.quatrevieux.araknemu.common.session.SessionLog;
import fr.quatrevieux.araknemu.common.session.SessionLogService;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.network.session.ConfigurableSession;
import fr.quatrevieux.araknemu.core.network.util.DummyChannel;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.spectator.Spectator;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.characteristic.event.CharacteristicsChanged;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class GameSessionTest extends GameBaseCase {
    @Test
    void account() throws ContainerException {
        GameSession session = new GameSession(new ConfigurableSession(new DummyChannel()));

        GameAccount account = new GameAccount(
            new Account(1),
            container.get(AccountService.class),
            1
        );

        session.attach(account);
        assertSame(account, session.account());
    }

    @Test
    void player() throws ContainerException, SQLException {
        GameSession session = new GameSession(new ConfigurableSession(new DummyChannel()));

        GamePlayer player = makeSimpleGamePlayer(10);

        session.setPlayer(player);

        assertSame(player, session.player());

        assertThrows(IllegalStateException.class, () -> session.setPlayer(player));

        session.setPlayer(null);
        assertNull(session.player());
    }

    @Test
    void exploration() throws ContainerException, SQLException {
        GameSession session = new GameSession(new ConfigurableSession(new DummyChannel()));

        ExplorationPlayer player = new ExplorationPlayer(gamePlayer());

        session.setExploration(player);

        assertSame(player, session.exploration());
    }

    @Test
    void fighter() throws SQLException, ContainerException {
        GameSession session = new GameSession(new ConfigurableSession(new DummyChannel()));

        assertNull(session.fighter());

        PlayerFighter fighter = new PlayerFighter(gamePlayer());
        session.setFighter(fighter);

        assertSame(fighter, session.fighter());
    }

    @Test
    void spectator() throws SQLException, ContainerException {
        GameSession session = new GameSession(new ConfigurableSession(new DummyChannel()));

        assertNull(session.spectator());

        Spectator spectator = new Spectator(gamePlayer(), null);
        session.setSpectator(spectator);

        assertSame(spectator, session.spectator());
    }

    @Test
    void log() throws ContainerException {
        login();
        GameSession session = new GameSession(new ConfigurableSession(new DummyChannel()));

        assertNull(session.log());

        SessionLog log = container.get(SessionLogService.class).create(this.session);
        session.setLog(log);

        assertSame(log, session.log());
    }

    @Test
    void dispatchWithPlayer() throws ContainerException, SQLException {
        GameSession session = new GameSession(new ConfigurableSession(new DummyChannel()));
        GamePlayer player = makeSimpleGamePlayer(10);
        session.setPlayer(player);

        AtomicReference<CharacteristicsChanged> ref = new AtomicReference<>();
        player.dispatcher().add(CharacteristicsChanged.class, ref::set);

        CharacteristicsChanged event = new CharacteristicsChanged();
        session.dispatch(event);

        assertSame(event, ref.get());
    }

    @Test
    void dispatchWithExploration() throws ContainerException, SQLException {
        GameSession session = new GameSession(new ConfigurableSession(new DummyChannel()));
        GamePlayer player = makeSimpleGamePlayer(10);
        session.setPlayer(player);

        ExplorationPlayer exploration = explorationPlayer();
        session.setExploration(exploration);

        AtomicReference<CharacteristicsChanged> ref = new AtomicReference<>();
        player.dispatcher().add(CharacteristicsChanged.class, ref::set);

        AtomicReference<CharacteristicsChanged> ref2 = new AtomicReference<>();
        exploration.dispatcher().add(CharacteristicsChanged.class, ref2::set);

        CharacteristicsChanged event = new CharacteristicsChanged();
        session.dispatch(event);

        assertSame(event, ref.get());
        assertSame(event, ref2.get());
    }

    @Test
    void dispatchWithSpectator() throws ContainerException, SQLException {
        GameSession session = new GameSession(new ConfigurableSession(new DummyChannel()));
        GamePlayer player = makeSimpleGamePlayer(10);
        session.setPlayer(player);

        Spectator spectator = new Spectator(player, null);
        session.setSpectator(spectator);

        AtomicReference<CharacteristicsChanged> ref = new AtomicReference<>();
        player.dispatcher().add(CharacteristicsChanged.class, ref::set);

        AtomicReference<CharacteristicsChanged> ref2 = new AtomicReference<>();
        spectator.dispatcher().add(CharacteristicsChanged.class, ref2::set);

        CharacteristicsChanged event = new CharacteristicsChanged();
        session.dispatch(event);

        assertSame(event, ref.get());
        assertSame(event, ref2.get());
    }

    @Test
    void string() throws SQLException {
        GameSession session = new GameSession(new ConfigurableSession(new DummyChannel()));

        assertEquals("ip=127.0.0.1", session.toString());

        GamePlayer player = makeSimpleGamePlayer(10);

        session.attach(player.account());
        assertEquals("ip=127.0.0.1; account=10010", session.toString());

        session.setPlayer(player);

        assertEquals("ip=127.0.0.1; account=10010; player=10; position=(10540, 210)", session.toString());

        ExplorationPlayer exploration = explorationPlayer();
        session.setExploration(exploration);

        assertEquals("ip=127.0.0.1; account=10010; player=10; position=(10540, 210); state=exploring", session.toString());

        session.setExploration(null);
        session.setFighter(new PlayerFighter(player));

        assertEquals("ip=127.0.0.1; account=10010; player=10; position=(10540, 210); state=fighting", session.toString());

        session.setFighter(null);
        session.setSpectator(new Spectator(player, null));

        assertEquals("ip=127.0.0.1; account=10010; player=10; position=(10540, 210); state=spectator", session.toString());
    }
}
