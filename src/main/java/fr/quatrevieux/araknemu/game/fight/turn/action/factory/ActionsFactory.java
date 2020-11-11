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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.turn.action.factory;

import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.CastActionFactory;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveActionFactory;

/**
 * Factory for fight actions
 */
public interface ActionsFactory {
    /**
     * Create a fight action
     *
     * @param action The action type
     * @param arguments The arguments of the GA packet
     *
     * @return The new action
     *
     * @throws FightException When cannot create the action
     */
    public Action create(ActionType action, String[] arguments);

    /**
     * Get the factory for spell cast action
     */
    public CastActionFactory cast();

    /**
     * Get the factory for close combat action
     */
    public FightActionFactory closeCombat();

    /**
     * Get the factory for move action
     */
    public MoveActionFactory move();
}
