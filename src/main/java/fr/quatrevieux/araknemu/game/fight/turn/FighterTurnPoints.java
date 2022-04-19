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

package fr.quatrevieux.araknemu.game.fight.turn;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.event.ActionPointsUsed;
import fr.quatrevieux.araknemu.game.fight.turn.event.MovementPointsUsed;
import org.checkerframework.checker.index.qual.NonNegative;

/**
 * Handle fighter turn points (AP / MP)
 */
public final class FighterTurnPoints implements TurnPoints {
    private final Fight fight;
    private final Fighter fighter;

    private @NonNegative int movementPoints;
    private @NonNegative int actionPoints;

    private @NonNegative int usedMovementPoints;
    private @NonNegative int usedActionPoints;

    public FighterTurnPoints(Fight fight, Fighter fighter) {
        this.fight = fight;
        this.fighter = fighter;

        this.movementPoints = Math.max(fighter.characteristics().get(Characteristic.MOVEMENT_POINT), 0);
        this.actionPoints = Math.max(fighter.characteristics().get(Characteristic.ACTION_POINT), 0);
    }

    @Override
    public @NonNegative int movementPoints() {
        return Math.max(movementPoints - usedMovementPoints, 0);
    }

    /**
     * Remove movement points
     *
     * @param points Points to remove
     */
    public void useMovementPoints(@NonNegative int points) {
        usedMovementPoints += points;

        fight.dispatch(new MovementPointsUsed(fighter, points));
    }

    @Override
    public void addMovementPoints(@NonNegative int value) {
        movementPoints += value;
    }

    @Override
    public @NonNegative int removeMovementPoints(@NonNegative int value) {
        if (value > movementPoints()) {
            value = movementPoints();
        }

        movementPoints -= value;

        return value;
    }

    @Override
    public @NonNegative int actionPoints() {
        return Math.max(actionPoints - usedActionPoints, 0);
    }

    /**
     * Remove action points
     *
     * @param points Points to remove
     */
    public void useActionPoints(@NonNegative int points) {
        usedActionPoints += points;

        fight.dispatch(new ActionPointsUsed(fighter, points));
    }

    @Override
    public void addActionPoints(@NonNegative int value) {
        actionPoints += value;
    }

    @Override
    public @NonNegative int removeActionPoints(@NonNegative int value) {
        if (value > actionPoints()) {
            value = actionPoints();
        }

        actionPoints -= value;

        return value;
    }

    @Override
    public @NonNegative int usedActionPoints() {
        return usedActionPoints;
    }

    @Override
    public @NonNegative int usedMovementPoints() {
        return usedMovementPoints;
    }
}
