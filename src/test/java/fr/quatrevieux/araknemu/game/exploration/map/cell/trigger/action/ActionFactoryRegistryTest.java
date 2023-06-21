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

package fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.teleport.Teleport;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.teleport.TeleportFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ActionFactoryRegistryTest extends GameBaseCase {
    private ActionFactoryRegistry registry;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        registry = new ActionFactoryRegistry();
    }

    @Test
    void createActionNotFound() {
        assertThrows(NoSuchElementException.class, () -> registry.create(new MapTrigger(10300, 123, 15, "", "")));
    }

    @Test
    void registerAndCreate() throws ContainerException {
        assertSame(registry, registry.register(0, new TeleportFactory(container.get(ExplorationMapService.class))));

        assertInstanceOf(Teleport.class, registry.create(new MapTrigger(10300, 123, 0, "10340,456", "-1")));
    }
}