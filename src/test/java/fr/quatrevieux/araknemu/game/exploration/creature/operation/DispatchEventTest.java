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

package fr.quatrevieux.araknemu.game.exploration.creature.operation;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertSame;

class DispatchEventTest extends GameBaseCase {
    @Test
    void onExplorationPlayer() throws SQLException, ContainerException {
        Object event = new Object();
        AtomicReference<Object> ref = new AtomicReference<>();

        explorationPlayer().dispatcher().add(Object.class, ref::set);

        explorationPlayer().apply(new DispatchEvent(event));

        assertSame(event, ref.get());
    }

    @Test
    void onNpc() throws SQLException, ContainerException {
        dataSet
            .pushMaps()
            .pushNpcs()
            .pushAreas()
            .pushSubAreas()
        ;

        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        GameNpc npc = GameNpc.class.cast(map.creature(-47204));

        npc.apply(new DispatchEvent(new Object()));
    }
}
