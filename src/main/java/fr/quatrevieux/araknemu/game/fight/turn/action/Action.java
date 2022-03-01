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

package fr.quatrevieux.araknemu.game.fight.turn.action;

import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;

import java.time.Duration;

/**
 * Action for a fight turn
 *
 * Lifecycle :
 * - Check if the action can be proceeded using {@link Action#validate(Turn)}
 * - If valid, call {@link Action#start()} and get the result
 * - If the result is not successful, directly call {@link ActionResult#apply(FightTurn)} and stop the process
 * - If successful, wait for termination (i.e. {@link FightTurn#terminate()}
 * - On termination, call {@link ActionResult#apply(FightTurn)}
 */
public interface Action {
    /**
     * Validate the action before start
     *
     * @param turn The active fighter turn
     */
    public boolean validate(Turn turn);

    /**
     * Start to perform the action
     */
    public ActionResult start();

    /**
     * Get the action performer
     */
    public ActiveFighter performer();

    /**
     * Get the action type
     */
    public ActionType type();

    /**
     * Get the maximum action duration (will invoke end when this duration is reached)
     */
    public Duration duration();
}
