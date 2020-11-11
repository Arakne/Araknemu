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

/**
 * Handle fighter turn points (AP / MP)
 */
final public class FighterTurnPoints implements TurnPoints {
    final private Fight fight;
    final private Fighter fighter;

    private int movementPoints;
    private int actionPoints;

    private int usedMovementPoints;
    private int usedActionPoints;

    public FighterTurnPoints(Fight fight, Fighter fighter) {
        this.fight = fight;
        this.fighter = fighter;

        this.movementPoints = fighter.characteristics().get(Characteristic.MOVEMENT_POINT);
        this.actionPoints = fighter.characteristics().get(Characteristic.ACTION_POINT);
    }

    @Override
    public int movementPoints() {
        return movementPoints - usedMovementPoints;
    }

    /**
     * Remove movement points
     *
     * @param points Points to remove
     */
    public void useMovementPoints(int points) {
        usedMovementPoints += points;

        fight.dispatch(new MovementPointsUsed(fighter, points));
    }

    @Override
    public void addMovementPoints(int value) {
        movementPoints += value;
    }

    @Override
    public int removeMovementPoints(int value) {
        if (value > movementPoints()) {
            value = movementPoints();
        }

        movementPoints -= value;

        return value;
    }

    @Override
    public int actionPoints() {
        return actionPoints - usedActionPoints;
    }

    /**
     * Remove action points
     *
     * @param points Points to remove
     */
    public void useActionPoints(int points) {
        usedActionPoints += points;

        fight.dispatch(new ActionPointsUsed(fighter, points));
    }

    @Override
    public void addActionPoints(int value) {
        actionPoints += value;
    }

    @Override
    public int removeActionPoints(int value) {
        if (value > actionPoints()) {
            value = actionPoints();
        }

        actionPoints -= value;

        return value;
    }
}
