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

package fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.teleport;

import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.CellAction;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.CellActionFactory;
import fr.quatrevieux.araknemu.util.Splitter;

/**
 * Create the teleport action
 */
public final class TeleportFactory implements CellActionFactory {
    private final ExplorationMapService service;

    public TeleportFactory(ExplorationMapService service) {
        this.service = service;
    }

    @Override
    public CellAction create(MapTrigger trigger) {
        final Splitter splitter = new Splitter(trigger.arguments(), ',');

        return new Teleport(
            service,
            trigger.cell(),
            new Position(
                splitter.nextNonNegativeInt(),
                splitter.nextNonNegativeInt()
            )
        );
    }
}
