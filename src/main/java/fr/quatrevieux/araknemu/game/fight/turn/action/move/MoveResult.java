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

package fr.quatrevieux.araknemu.game.fight.turn.action.move;

import fr.arakne.utils.maps.constant.Direction;
import fr.arakne.utils.maps.path.Path;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;

/**
 * The result of a {@link Move} action
 */
public interface MoveResult extends ActionResult {
    /**
     * Get the cost in AP of the action
     *
     * @see fr.quatrevieux.araknemu.game.fight.turn.TurnPoints#actionPoints()
     */
    public int lostActionPoints();

    /**
     * Get the cost in MP of the action
     *
     * @see fr.quatrevieux.araknemu.game.fight.turn.TurnPoints#movementPoints()
     */
    public int lostMovementPoints();

    /**
     * Get the validated path
     */
    public Path<FightCell> path();

    /**
     * Get the target cell
     */
    public FightCell target();

    /**
     * Get the final orientation
     */
    public Direction orientation();
}
