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

package fr.quatrevieux.araknemu.game.fight.turn;

import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.factory.ActionsFactory;

public interface Turn {
    /**
     * Get the current fighter
     */
    public ActiveFighter fighter();

    /**
     * Check if the turn is active
     */
    public boolean active();

    /**
     * Perform a fight action
     *
     * @param action The action to perform
     */
    public void perform(Action action) throws FightException;

    /**
     * Execute the action when the current is terminated
     * If there is no pending action, the action is immediately executed
     */
    public void later(Runnable nextAction);

    /**
     * Get the actions factory
     */
    public ActionsFactory actions();

    /**
     * Get the current fighter points
     */
    public TurnPoints points();

    /**
     * Stop the turn and start the next turn
     */
    public void stop();
}
