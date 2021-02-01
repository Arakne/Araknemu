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

package fr.quatrevieux.araknemu.game.exploration.interaction.action;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;

import java.util.EnumMap;

/**
 * Registry for all exploration game actions
 */
public final class ExplorationActionRegistry implements ActionFactory {
    public interface SelfRegisterable {
        /**
         * Auto-register the action factory into the action registry
         */
        public void register(ExplorationActionRegistry factory);
    }

    private final EnumMap<ActionType, ActionFactory> factories = new EnumMap<>(ActionType.class);

    public ExplorationActionRegistry(SelfRegisterable... actions) {
        for (SelfRegisterable action : actions) {
            action.register(this);
        }
    }

    @Override
    public Action create(ExplorationPlayer player, ActionType action, String[] arguments) {
        if (!factories.containsKey(action)) {
            throw new IllegalArgumentException("No factory found for game action : " + action);
        }

        return factories.get(action).create(player, action, arguments);
    }

    /**
     * Register a new exploration action into the registry
     *
     * @param type The action type
     * @param factory The factory
     */
    public void register(ActionType type, ActionFactory factory) {
        factories.put(type, factory);
    }
}
