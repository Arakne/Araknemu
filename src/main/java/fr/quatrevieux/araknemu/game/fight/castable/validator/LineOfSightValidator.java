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

package fr.quatrevieux.araknemu.game.fight.castable.validator;

import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.world.map.util.LineOfSight;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

/**
 * Validate the line of sight
 */
final public class LineOfSightValidator implements CastConstraintValidator {
    @Override
    public Error validate(FightTurn turn, Castable castable, FightCell target) {
        if (!castable.constraints().lineOfSight()) {
            return null;
        }

        LineOfSight<FightCell> lineOfSight = new LineOfSight<>(target.map());

        if (lineOfSight.between(turn.fighter().cell(), target)) {
            return null;
        }

        return Error.cantCastSightBlocked();
    }
}
