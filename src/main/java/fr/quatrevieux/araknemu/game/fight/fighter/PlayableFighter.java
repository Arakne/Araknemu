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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.fighter;

import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;

import java.util.function.Consumer;

/**
 * Base type for a fighter which can play a turn
 */
public interface PlayableFighter extends Fighter {
    /**
     * Get the current fighter turn
     *
     * @throws FightException If it's not the turn of the current fighter
     *
     * @see PlayableFighter#perform(Consumer) For perform action on fighter in a safe way (no exception)
     */
    public FightTurn turn();

    /**
     * Perform an action on the current active turn
     * The action will take as parameter the current turn
     *
     * If it's not the turn of the fighter, this method will not call the action
     *
     * @param action Action to perform
     */
    public void perform(Consumer<FightTurn> action);

    /**
     * Start to play the turn
     *
     * @param turn The fighter turn
     */
    public void play(FightTurn turn);

    /**
     * Stop the turn
     */
    public void stop();
}
