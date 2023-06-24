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

package fr.quatrevieux.araknemu.game.fight.spectator;

import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.spectator.event.SpectatorJoined;
import fr.quatrevieux.araknemu.game.fight.spectator.event.SpectatorLeaved;
import fr.quatrevieux.araknemu.game.fight.spectator.event.StartWatchFight;
import fr.quatrevieux.araknemu.game.fight.spectator.event.StopWatchFight;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class SpectatorTest extends FightBaseCase {
    @Test
    void startSession() throws SQLException {
        Fight fight = createSimpleFight(container.get(ExplorationMapService.class).load(10340));
        Spectator spectator = new Spectator(gamePlayer(), fight);

        gamePlayer().start(spectator);

        assertSame(spectator, session.spectator());
    }

    @Test
    void join() throws SQLException {
        Fight fight = createSimpleFight(container.get(ExplorationMapService.class).load(10340));
        Spectator spectator = new Spectator(gamePlayer(), fight);

        AtomicReference<StartWatchFight> swf = new AtomicReference<>();
        spectator.dispatcher().add(StartWatchFight.class, swf::set);

        AtomicReference<SpectatorJoined> sj = new AtomicReference<>();
        fight.dispatcher().add(SpectatorJoined.class, sj::set);

        spectator.join();

        assertSame(spectator, sj.get().spectator());
        assertSame(spectator, session.spectator());

        assertNotNull(swf.get());

        fight.send("foo");
        requestStack.assertLast("foo");
    }

    @Test
    void stopSession() throws SQLException {
        Fight fight = createSimpleFight(container.get(ExplorationMapService.class).load(10340));
        Spectator spectator = new Spectator(gamePlayer(), fight);

        spectator.join();
        gamePlayer().stop(spectator);

        assertNull(session.spectator());
    }

    @Test
    void stop() throws SQLException {
        Fight fight = createSimpleFight(container.get(ExplorationMapService.class).load(10340));
        Spectator spectator = new Spectator(gamePlayer(), fight);

        spectator.join();
        spectator.stop();

        assertNull(session.spectator());
    }

    @Test
    void leave() throws SQLException {
        Fight fight = createSimpleFight(container.get(ExplorationMapService.class).load(10340));
        Spectator spectator = new Spectator(gamePlayer(), fight);

        AtomicReference<StopWatchFight> swf = new AtomicReference<>();
        spectator.dispatcher().add(StopWatchFight.class, swf::set);

        AtomicReference<SpectatorLeaved> sl = new AtomicReference<>();
        fight.dispatcher().add(SpectatorLeaved.class, sl::set);

        spectator.join();
        spectator.leave();

        assertNull(session.spectator());
        assertSame(spectator, sl.get().spectator());
        assertNotNull(swf.get());

        requestStack.clear();
        fight.send("foo");

        requestStack.assertEmpty();
    }

    @Test
    void getters() throws SQLException {
        Fight fight = createSimpleFight(container.get(ExplorationMapService.class).load(10340));
        Spectator spectator = new Spectator(gamePlayer(), fight);

        assertSame(fight, spectator.fight());
        assertEquals("Bob", spectator.name());
        assertSame(gamePlayer().properties(), spectator.properties());
    }

    @Test
    void equalsAndHashCode() throws Exception {
        Fight fight = createSimpleFight(container.get(ExplorationMapService.class).load(10340));
        Spectator spectator = new Spectator(gamePlayer(), fight);

        assertEquals(spectator, spectator);
        assertEquals(spectator, new Spectator(gamePlayer(), fight));
        assertEquals(new Spectator(gamePlayer(), fight), spectator);

        assertNotEquals(spectator, new Object());
        assertNotEquals(spectator, null);
        assertNotEquals(spectator, new Spectator(makeSimpleGamePlayer(5), fight));

        assertEquals(spectator.hashCode(), spectator.hashCode());
        assertEquals(spectator.hashCode(), new Spectator(gamePlayer(), fight).hashCode());
        assertNotEquals(spectator.hashCode(), new Spectator(makeSimpleGamePlayer(10), fight).hashCode());
    }
}
