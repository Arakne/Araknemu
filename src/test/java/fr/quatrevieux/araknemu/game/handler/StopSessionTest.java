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

package fr.quatrevieux.araknemu.game.handler;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.handler.event.Disconnected;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.core.network.SessionClosed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class StopSessionTest extends FightBaseCase {
    private StopSession handler;
    private AccountService accountService;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();

        handler = new StopSession();
        accountService = container.get(AccountService.class);
    }

    @Test
    void withAttachedAccount() throws ContainerException {
        GameAccount account = new GameAccount(
            new Account(2),
            container.get(AccountService.class),
            1
        );
        account.attach(session);

        handler.handle(session, new SessionClosed());

        assertFalse(session.isLogged());
        assertFalse(account.isLogged());
        assertFalse(accountService.isLogged(account.id()));
        assertNull(session.account());
    }

    @Test
    void withSimplePlayer() throws SQLException, ContainerException {
        GamePlayer player = gamePlayer();

        handler.handle(session, new SessionClosed());

        assertFalse(session.isLogged());
        assertFalse(player.account().isLogged());
        assertFalse(accountService.isLogged(player.account().id()));
        assertNull(session.account());
        assertNull(session.player());
    }

    @Test
    void withExplorationPlayer() throws SQLException, ContainerException {
        ExplorationPlayer player = explorationPlayer();
        ExplorationMap map = player.map();

        handler.handle(session, new SessionClosed());

        assertFalse(session.isLogged());
        assertFalse(player.account().isLogged());
        assertFalse(accountService.isLogged(player.account().id()));
        assertNull(session.account());
        assertNull(session.player());
        assertNull(session.exploration());
        assertFalse(map.creatures().contains(player));
    }

    @Test
    void withPlayerWillSaveThePlayer() throws SQLException, ContainerException, NoSuchFieldException, IllegalAccessException {
        GamePlayer player = gamePlayer(true);

        player.setPosition(new Position(1234, 56));
        this.<Player>readField(player, "entity").setBoostPoints(12);

        int playerid = gamePlayer().id();

        handler.handle(session, new SessionClosed());

        Player savedPlayer = container.get(PlayerRepository.class).get(new Player(playerid));

        assertEquals(new Position(1234, 56), savedPlayer.position());
        assertEquals(12, savedPlayer.boostPoints());
    }

    @Test
    void withFighterWillLeaveTheFight() throws Exception {
        Fight fight = createFight();
        PlayerFighter fighter = player.fighter();

        handler.handle(session, new SessionClosed());

        assertNull(session.fighter());
        assertFalse(fight.fighters().contains(fighter));
    }

    @Test
    void withFighterWillLeaveTheFightOnActiveFight() throws Exception {
        Fight fight = createFight();
        PlayerFighter fighter = player.fighter();

        fight.state(PlacementState.class).startFight();

        handler.handle(session, new SessionClosed());

        assertNull(session.fighter());
        assertFalse(fight.active());
        assertTrue(fighter.dead());
    }

    @Test
    void dispatchDisconnectedOnlyOnce() throws SQLException, ContainerException {
        explorationPlayer();

        AtomicInteger count = new AtomicInteger();
        gamePlayer().dispatcher().add(Disconnected.class, e -> count.incrementAndGet());

        handler.handle(session, new SessionClosed());

        assertEquals(1, count.get());
    }
}
