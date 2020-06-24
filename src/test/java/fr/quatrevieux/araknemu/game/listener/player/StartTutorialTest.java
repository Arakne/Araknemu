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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.listener.player;

import fr.quatrevieux.araknemu.data.living.entity.account.ConnectionLog;
import fr.quatrevieux.araknemu.data.living.repository.account.ConnectionLogRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.player.event.GameJoined;
import fr.quatrevieux.araknemu.network.game.out.tutorial.GameBegin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.Instant;

class StartTutorialTest extends GameBaseCase {
    private StartTutorial listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new StartTutorial(gamePlayer());
    }

    @Test
    void onNewPlayer() {
        listener.on(new GameJoined());

        requestStack.assertLast(new GameBegin());
    }

    @Test
    void onNotNewPlayer() throws SQLException {
        ConnectionLog log = dataSet.push(new ConnectionLog(gamePlayer().account().id(), Instant.now(), ""));
        log.setPlayerId(gamePlayer().id());
        log.setEndDate(Instant.now());
        container.get(ConnectionLogRepository.class).save(log);

        listener.on(new GameJoined());

        requestStack.assertEmpty();
    }
}
