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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.connector.RealmConnector;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import fr.quatrevieux.araknemu.realm.host.GameHost;
import fr.quatrevieux.araknemu.util.ExecutorFactory;
import io.github.artsok.RepeatedIfExceptionsTest;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SavingServiceTest extends GameBaseCase {
    @Test
    void executeFunctional() throws SQLException, InterruptedException {
        SavingService service = container.get(SavingService.class);
        RealmConnector connector = container.get(RealmConnector.class);
        GamePlayer player = gamePlayer(true);

        player.setPosition(new Position(123, 45));
        requestStack.clear();

        assertTrue(service.execute());

        requestStack.assertAll(
            Error.saveInProgress(),
            Error.saveTerminated()
        );

        assertEquals(new Position(123, 45), dataSet.refresh(new Player(player.id())).position());
        Mockito.verify(connector).updateState(2, GameHost.State.SAVING, false);
        Mockito.verify(connector).updateState(2, GameHost.State.ONLINE, true);
    }

    @Test
    void executeShouldReturnFalseOnSaving() throws Exception {
        ExecutorFactory.disableDirectExecution();
        gamePlayer(true);
        makeOtherPlayer(1);
        SavingService service = container.get(SavingService.class);

        assertTrue(service.execute());
        assertFalse(service.execute());
    }

    @Test
    void autosave() throws SQLException, InterruptedException {
        setConfigValue("autosave.interval", "0.01s");
        SavingService service = new SavingService(container.get(PlayerService.class), container.get(GameConfiguration.class), container.get(Dispatcher.class));
        GamePlayer player = gamePlayer(true);

        player.setPosition(new Position(123, 45));

        service.init(Mockito.mock(Logger.class));

        Thread.sleep(100);

        assertEquals(new Position(123, 45), dataSet.refresh(new Player(player.id())).position());
    }
}
