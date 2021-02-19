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

import fr.arakne.utils.maps.path.Path;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveStatus;

/**
 * Successful move result
 */
final public class MoveFailed extends MoveStatus {
    final private int lostActionPoints;

    public MoveFailed(Fighter performer, Path<FightCell> path, int lostActionPoints) {
        super(performer, path);
        this.lostActionPoints = lostActionPoints;
    }

    @Override
    public int action() {
        return ActionType.CANT_MOVEOUT.id();
    }

    @Override
    public boolean success() {
        return false;
    }

    @Override
    public int lostActionPoints() {
        return lostActionPoints;
    }
}
