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

package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;

import java.util.Optional;

/**
 * Generates actions for the AI
 */
public interface ActionGenerator {
    /**
     * Initialize the action generator when turn starts
     * This method must be called before {@link ActionGenerator#generate(AI, AiActionFactory)}
     */
    public void initialize(AI ai);

    /**
     * Try to generate an action
     *
     * If the action cannot be generated (not enough points, or not available), an empty optional is returned
     * Return an empty optional will execute the next action on the AI, or stop the turn if there is no valid actions
     */
    public <A extends Action> Optional<A> generate(AI ai, AiActionFactory<A> actions);
}
