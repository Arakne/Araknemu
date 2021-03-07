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

/**
 * Failed move result
 */
public final class MoveFailed implements MoveResult {
    private final int lostActionPoints;
    private final Fighter performer;

    public MoveFailed(Fighter performer, int lostActionPoints) {
        this.performer = performer;
        this.lostActionPoints = lostActionPoints;
    }

    @Override
    public int action() {
        return 104;
    }

    @Override
    public boolean success() {
        return false;
    }

    @Override
    public int lostActionPoints() {
        return lostActionPoints;
    }

    @Override
    public Path<FightCell> path() {
        return null;
    }

    @Override
    public Fighter performer() {
        return performer;
    }

    @Override
    public Direction orientation() {
        return performer.orientation();
    }

    @Override
    public int movementPointCost() {
        return performer.fight().turnList().current().get().points().movementPoints();
    }

    @Override
    public FightCell target() {
        return null;
    }

    @Override
    public Object[] arguments() {
        return new Object[0];
    }
}
