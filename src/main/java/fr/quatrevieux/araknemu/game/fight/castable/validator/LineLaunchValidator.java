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

package fr.quatrevieux.araknemu.game.fight.castable.validator;

import fr.arakne.utils.maps.constant.Direction;
import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

/**
 * Check if spell should be cast in line launch
 */
public final class LineLaunchValidator implements CastConstraintValidator {
    @Override
    public Error validate(Turn turn, Castable castable, FightCell target) {
        if (!castable.constraints().lineLaunch()) {
            return null;
        }

        if (castable.constraints().range().max() < 2 || target.equals(turn.fighter().cell())) {
            return null;
        }

        final int mapWidth = target.map().dimensions().width();

        for (Direction direction : Direction.values()) {
            if (!direction.restricted()) {
                continue;
            }

            final int diff = Math.abs(turn.fighter().cell().id() - target.id());
            final int inc  = direction.nextCellIncrement(mapWidth);

            // cell ids diff is multiple of inc => cells are in same line
            if (diff / inc < (mapWidth * 2 + 1) && diff % inc == 0) {
                return null;
            }
        }

        return Error.cantCastLineLaunch();
    }
}
