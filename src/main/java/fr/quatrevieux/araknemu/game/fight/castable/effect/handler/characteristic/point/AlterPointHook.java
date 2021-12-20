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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.point;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.AlterCharacteristicHook;
import fr.quatrevieux.araknemu.game.fight.turn.TurnPoints;

/**
 * Buff hook for handle altering turn point characteristics (i.e. {@link Characteristic#ACTION_POINT} and {@link Characteristic#MOVEMENT_POINT})
 * This hook will also update point of the current active turn, if applicable
 *
 * Use factory methods for create the hook instance
 */
public class AlterPointHook extends AlterCharacteristicHook {
    private final Fight fight;
    private final TurnPointsModifier modifier;

    protected AlterPointHook(Fight fight, Characteristic characteristic, int multiplier, TurnPointsModifier modifier) {
        super(fight, characteristic, multiplier);

        this.fight = fight;
        this.modifier = modifier;
    }

    @Override
    public void onBuffStarted(Buff buff) {
        super.onBuffStarted(buff);

        fight.turnList().current()
            .filter(turn -> turn.fighter().equals(buff.target()))
            .ifPresent(turn -> modifier.modify(turn.points(), buff.effect().min()))
        ;
    }

    /**
     * Hook for add a {@link Characteristic#MOVEMENT_POINT}
     *
     * @see TurnPoints#addMovementPoints(int)
     * @see Characteristic#MOVEMENT_POINT
     */
    public static AlterPointHook addMovementPoint(Fight fight) {
        return new AlterPointHook(fight, Characteristic.MOVEMENT_POINT, 1, TurnPoints::addMovementPoints);
    }

    /**
     * Hook for remove a {@link Characteristic#MOVEMENT_POINT}
     *
     * @see TurnPoints#removeMovementPoints(int)
     * @see Characteristic#MOVEMENT_POINT
     */
    public static AlterPointHook removeMovementPoint(Fight fight) {
        return new AlterPointHook(fight, Characteristic.MOVEMENT_POINT, -1, TurnPoints::removeMovementPoints);
    }

    /**
     * Hook for add a {@link Characteristic#ACTION_POINT}
     *
     * @see TurnPoints#addActionPoints(int)
     * @see Characteristic#ACTION_POINT
     */
    public static AlterPointHook addActionPoint(Fight fight) {
        return new AlterPointHook(fight, Characteristic.ACTION_POINT, 1, TurnPoints::addActionPoints);
    }

    /**
     * Hook for remove a {@link Characteristic#ACTION_POINT}
     *
     * @see TurnPoints#removeActionPoints(int)
     * @see Characteristic#ACTION_POINT
     */
    public static AlterPointHook removeActionPoint(Fight fight) {
        return new AlterPointHook(fight, Characteristic.ACTION_POINT, -1, TurnPoints::removeActionPoints);
    }

    @FunctionalInterface
    public interface TurnPointsModifier {
        /**
         * Modify the given turn points with the given value
         *
         * @param value Value to modify. Always a positive number
         */
        public void modify(TurnPoints points, int value);
    }
}
