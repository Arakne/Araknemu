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

import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Registry for all actions factories
 */
public final class ActionFactoryRegistry implements CellActionFactory {
    private final Map<Integer, CellActionFactory> factories = new HashMap<>();

    @Override
    public CellAction create(MapTrigger trigger) {
        final CellActionFactory factory = factories.get(trigger.action());

        if (factory == null) {
            throw new NoSuchElementException("Cannot found cell action " + trigger.action());
        }

        return factory.create(trigger);
    }

    /**
     * Register a new factory into the registry
     *
     * @param action The action id
     * @param factory The factory
     */
    public ActionFactoryRegistry register(int action, CellActionFactory factory) {
        factories.put(action, factory);

        return this;
    }
}
