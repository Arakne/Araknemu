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

package fr.quatrevieux.araknemu.game.fight.turn.action.move.validators;

import fr.arakne.utils.maps.path.PathStep;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldObjects;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.Move;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveResult;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveSuccess;

/**
 * Stop the move if it enters into a battlefield object with enter effect
 *
 * @see BattlefieldObjects#shouldStopMovement(FightCell)
 */
public final class StopOnBattlefieldObjectValidator implements FightPathValidator {
    @Override
    public MoveResult validate(Move move, MoveResult result) {
        final FightMap map = move.performer().cell().map();
        final BattlefieldObjects objects = map.objects();

        // Do not check the first cell which is the current one
        // nor the last, because truncate on the last step does nothing
        for (int i = 1; i < result.path().size() - 1; i++) {
            final PathStep<FightCell> step = result.path().get(i);

            // The step in on object area
            if (objects.shouldStopMovement(step.cell())) {
                // Truncate the path until the current cell (index + 1 because the argument is a size)
                return new MoveSuccess(move.performer(), result.path().truncate(i + 1));
            }
        }

        return result;
    }
}
