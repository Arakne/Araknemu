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
 * Copyright (c) 2017-2021 Vincent Quatrevieux, Jean-Alexandre Valentin
 */

package fr.quatrevieux.araknemu.game.listener.player;

import fr.quatrevieux.araknemu.core.network.SessionClosed;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.event.StartExploration;
import fr.quatrevieux.araknemu.game.exploration.event.StopExploration;
import fr.quatrevieux.araknemu.game.handler.StopSession;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.info.StartLifeTimer;
import fr.quatrevieux.araknemu.network.game.out.info.StopLifeTimer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class LifeRegenerationTest extends GameBaseCase {
    private ExplorationPlayer player;
    private GamePlayer gamePlayer;
    private StopSession handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        player = makeExplorationPlayer(
            gamePlayer = gamePlayer()
        );
        handler = new StopSession();
        requestStack.clear();

    }

    @Test
    void onStartExploration() {
        player.dispatch(new StartExploration(player));
        requestStack.assertContains(StartLifeTimer.class);
    }

    @Test
    void onStopExploration() {
        player.dispatch(new StopExploration(session));
        requestStack.assertContains(StopLifeTimer.class);
    }

    @Test
    void saveCorrectLifeWhenSessionClosed() throws Exception{
        player.player().properties().life().set(5);
        player.player().properties().life().startLifeRegeneration(20);
        Thread.sleep(40);

        handler.handle(session, new SessionClosed());

        ExplorationPlayer saved = makeExplorationPlayer(gamePlayer);
        assertEquals(7, saved.properties().life().current());
    }
}
