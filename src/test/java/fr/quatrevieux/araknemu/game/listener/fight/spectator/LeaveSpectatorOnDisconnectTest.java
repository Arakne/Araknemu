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

package fr.quatrevieux.araknemu.game.listener.fight.spectator;

import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.spectator.event.SpectatorLeaved;
import fr.quatrevieux.araknemu.game.fight.spectator.Spectator;
import fr.quatrevieux.araknemu.game.fight.spectator.SpectatorFactory;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.handler.event.Disconnected;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class LeaveSpectatorOnDisconnectTest extends FightBaseCase {
    @Test
    void onDisconnected() throws Exception {
        Fight fight = createSimpleFight(container.get(ExplorationMapService.class).load(10340));
        fight.state(PlacementState.class).startFight();

        Spectator spectator = container.get(SpectatorFactory.class).create(gamePlayer(), fight);
        spectator.join();

        AtomicReference<SpectatorLeaved> ref = new AtomicReference<>();
        fight.dispatcher().add(SpectatorLeaved.class, ref::set);

        new LeaveSpectatorOnDisconnect(spectator).on(new Disconnected());

        assertNull(session.spectator());
        assertSame(spectator, ref.get().spectator());
        requestStack.clear();

        fight.send("foo");
        requestStack.assertEmpty();
    }
}
