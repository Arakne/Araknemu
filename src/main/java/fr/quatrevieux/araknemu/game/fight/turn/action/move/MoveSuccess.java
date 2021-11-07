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

package fr.quatrevieux.araknemu.game.fight.turn.action.move;

import fr.arakne.utils.maps.constant.Direction;
import fr.arakne.utils.maps.path.Path;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;

/**
 * Successful move result
 * A path can be truncated due to enemies on path, which will also result to a MoveSuccess result
 */
public final class MoveSuccess implements MoveResult {
    private final Fighter performer;
    private final Path<FightCell> path;

    /**
     * @param performer The current fighter which perform the action
     * @param path The new path
     */
    public MoveSuccess(Fighter performer, Path<FightCell> path) {
        this.performer = performer;
        this.path = path;
    }

    @Override
    public int action() {
        return ActionType.MOVE.id();
    }

    @Override
    public boolean success() {
        return true;
    }

    @Override
    public int lostActionPoints() {
        return 0;
    }

    @Override
    public Fighter performer() {
        return performer;
    }

    @Override
    public Object[] arguments() {
        return new Object[] { path.encodeWithStartCell() };
    }

    @Override
    public FightCell target() {
        return path.target();
    }

    @Override
    public Direction orientation() {
        return path.last().direction();
    }

    @Override
    public int lostMovementPoints() {
        return path.size() - 1; // The path contains the current fighter's cell
    }

    @Override
    public Path<FightCell> path() {
        return path;
    }
}
