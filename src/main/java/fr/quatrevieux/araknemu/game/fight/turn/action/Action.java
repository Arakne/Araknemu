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
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

import java.time.Duration;

/**
 * Action for a fight turn
 */
public interface Action {
    /**
     * Validate the action before start
     */
    public boolean validate();

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
     * The action is failed
     */
    public void failed();

    /**
     * End the action normally (i.e. the action is successfully done)
     */
    public void end();

    /**
     * Get the maximum action duration (will invoke end when this duration is reached)
     */
    public Duration duration();
}
