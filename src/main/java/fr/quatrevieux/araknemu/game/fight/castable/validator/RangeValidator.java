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

import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Validate the cast range
 */
public final class RangeValidator implements CastConstraintValidator {
    @Override
    public boolean check(Turn turn, Castable castable, BattlefieldCell target) {
        final int distance = turn.fighter().cell().coordinate().distance(target);

        Interval range = castable.constraints().range();

        if (castable.modifiableRange()) {
            range = range.modify(turn.fighter().characteristics().get(Characteristic.SIGHT_BOOST));
        }

        return range.contains(distance);
    }

    @Override
    public @Nullable Error validate(Turn turn, Castable castable, BattlefieldCell target) {
        final int distance = turn.fighter().cell().coordinate().distance(target);

        Interval range = castable.constraints().range();

        if (castable.modifiableRange()) {
            range = range.modify(turn.fighter().characteristics().get(Characteristic.SIGHT_BOOST));
        }

        if (!range.contains(distance)) {
            return Error.cantCastBadRange(range, distance);
        }

        return null;
    }
}
