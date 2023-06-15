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
import fr.arakne.utils.maps.path.PathStep;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;

import java.util.Collections;

/**
 * The fighter has been tackle during its move action
 * He'll lose all its MP, and some AP
 */
public final class MoveFailed implements MoveResult {
    private final PlayableFighter performer;
    private final int lostActionPoints;

    /**
     * @param performer The current fighter
     * @param lostActionPoints Number of lost action points
     */
    public MoveFailed(PlayableFighter performer, int lostActionPoints) {
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
    public boolean secret() {
        return performer.hidden();
    }

    @Override
    public int lostActionPoints() {
        return lostActionPoints;
    }

    @Override
    public Path<FightCell> path() {
        return new Path<>(
            performer.cell().map().decoder(),
            Collections.singletonList(new PathStep<>(performer.cell(), Direction.EAST))
        );
    }

    @Override
    public PlayableFighter performer() {
        return performer;
    }

    @Override
    public Direction orientation() {
        return performer.orientation();
    }

    @Override
    public int lostMovementPoints() {
        return performer.turn().points().movementPoints();
    }

    @Override
    public FightCell target() {
        return performer.cell();
    }

    @Override
    public Object[] arguments() {
        return new Object[0];
    }

    @Override
    public void apply(FightTurn turn) {
        if (lostActionPoints > 0) {
            turn.points().useActionPoints(lostActionPoints);
        }

        turn.points().useMovementPoints(turn.points().movementPoints());
    }
}
