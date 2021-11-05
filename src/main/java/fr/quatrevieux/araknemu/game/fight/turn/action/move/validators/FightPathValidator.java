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
 * Copyright (c) 2017-2021 Vincent Quatrevieux, Jean-Alexandre Valentin
 */

package fr.quatrevieux.araknemu.game.fight.turn.action.move.validators;

import fr.quatrevieux.araknemu.game.fight.turn.action.move.Move;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveResult;

/**
 * Validate a move action
 * Validator are called by {@link Move#start()} to truncate or invalidate a move
 */
public interface FightPathValidator {
    /**
     * Validate or filter the path and return the MoveResult
     *
     * @param move Move action
     * @param result MoveResult the result of the previous validator
     *
     * @return The filtered result. Can be same as parameter is the path is valid.
     */
    public MoveResult validate(Move move, MoveResult result);
}
