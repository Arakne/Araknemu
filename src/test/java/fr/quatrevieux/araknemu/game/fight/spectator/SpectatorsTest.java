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
import fr.quatrevieux.araknemu.game.listener.fight.spectator.SendSpectatorHasJoined;
import fr.quatrevieux.araknemu.network.game.out.fight.CancelFight;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class SpectatorsTest extends FightBaseCase {
    @Test
    void listeners() throws SQLException {
        Fight fight = createSimpleFight(container.get(ExplorationMapService.class).load(10340));

        assertTrue(fight.dispatcher().has(SendSpectatorHasJoined.class));
    }

    @Test
    void addShouldAddToFightAndDispatchSpectatorJoined() throws SQLException {
        Fight fight = createSimpleFight(container.get(ExplorationMapService.class).load(10340));
        fight.nextState();

        Spectator spectator = container.get(SpectatorFactory.class).create(gamePlayer(), fight);
        Spectators spectators = new Spectators(fight);

        AtomicReference<SpectatorJoined> ref = new AtomicReference<>();
        fight.dispatcher().add(SpectatorJoined.class, ref::set);

        spectators.add(spectator);
        assertSame(spectator, ref.get().spectator());

        spectators.send("foo");
        requestStack.assertLast("foo");
    }

    @Test
    void cannotAddTwice() throws SQLException {
        Fight fight = createSimpleFight(container.get(ExplorationMapService.class).load(10340));
        fight.nextState();

        Spectator spectator = container.get(SpectatorFactory.class).create(gamePlayer(), fight);
        Spectators spectators = new Spectators(fight);

        AtomicReference<SpectatorJoined> ref = new AtomicReference<>();
        fight.dispatcher().add(SpectatorJoined.class, ref::set);

        spectators.add(spectator);
        assertSame(spectator, ref.get().spectator());

        ref.set(null);
        assertThrowsWithMessage(IllegalStateException.class, "Spectator Bob is already added", () -> spectators.add(spectator));

        assertNull(ref.get());
    }

    @Test
    void removeShouldDispatchSpectatorLeaved() throws SQLException {
        Fight fight = createSimpleFight(container.get(ExplorationMapService.class).load(10340));
        fight.nextState();

        Spectator spectator = container.get(SpectatorFactory.class).create(gamePlayer(), fight);
        Spectators spectators = new Spectators(fight);

        AtomicReference<SpectatorLeaved> ref = new AtomicReference<>();
        fight.dispatcher().add(SpectatorLeaved.class, ref::set);

        spectators.add(spectator);
        spectators.remove(spectator);

        assertSame(spectator, ref.get().spectator());
        requestStack.clear();

        spectators.send("foo");
        requestStack.assertEmpty();
    }

    @Test
    void removeNotAllowedOnNonexistentSpectator() throws SQLException {
        Fight fight = createSimpleFight(container.get(ExplorationMapService.class).load(10340));
        fight.nextState();

        Spectator spectator = container.get(SpectatorFactory.class).create(gamePlayer(), fight);
        Spectators spectators = new Spectators(fight);

        AtomicReference<SpectatorLeaved> ref = new AtomicReference<>();
        fight.dispatcher().add(SpectatorLeaved.class, ref::set);

        assertThrowsWithMessage(IllegalStateException.class, "Spectator Bob is not present", () -> spectators.remove(spectator));

        assertNull(ref.get());
    }

    @Test
    void spectatorSessionsShouldBeStoppedOnFightStopWithoutRemoveFromFight() throws SQLException {
        Fight fight = createSimpleFight(container.get(ExplorationMapService.class).load(10340));
        fight.nextState();

        Spectator spectator = container.get(SpectatorFactory.class).create(gamePlayer(), fight);
        spectator.join();

        assertSame(spectator, session.spectator());

        fight.send("foo");
        requestStack.assertLast("foo");

        fight.stop();
        assertNull(session.spectator());

        fight.send("bar");
        requestStack.assertLast("bar");
    }

    @Test
    void spectatorShouldLeaveOnFightCancel() throws SQLException {
        Fight fight = createSimpleFight(container.get(ExplorationMapService.class).load(10340));
        fight.nextState();

        Spectator spectator = container.get(SpectatorFactory.class).create(gamePlayer(), fight);
        spectator.join();

        assertSame(spectator, session.spectator());

        requestStack.clear();
        fight.cancel(true);
        assertNull(session.spectator());
        requestStack.assertLast(new CancelFight());

        requestStack.clear();
        fight.send("bar");
        requestStack.assertEmpty();
    }

    @Test
    void dispatch() throws SQLException {
        Fight fight = createSimpleFight(container.get(ExplorationMapService.class).load(10340));
        fight.nextState();

        Spectator spectator = container.get(SpectatorFactory.class).create(gamePlayer(), fight);
        Spectators spectators = new Spectators(fight);
        spectators.add(spectator);
        gamePlayer().start(spectator);

        class Foo {}
        AtomicReference<Foo> ref = new AtomicReference<>();
        spectator.dispatcher().add(Foo.class, ref::set);

        Foo evt = new Foo();
        spectators.dispatch(evt);

        assertSame(evt, ref.get());
    }
}
