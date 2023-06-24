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
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Aggregates of constraints
 */
public final class ConstraintsAggregateValidator<T extends Castable> implements CastConstraintValidator<T> {
    private final CastConstraintValidator<? super T>[] validators;

    public ConstraintsAggregateValidator(CastConstraintValidator<? super T>[] validators) {
        this.validators = validators;
    }

    @Override
    public boolean check(Turn turn, T castable, BattlefieldCell target) {
        for (CastConstraintValidator<? super T> validator : validators) {
            if (!validator.check(turn, castable, target)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public @Nullable Object validate(Turn turn, T action, BattlefieldCell target) {
        for (CastConstraintValidator<? super T> validator : validators) {
            final Object error = validator.validate(turn, action, target);

            if (error != null) {
                return error;
            }
        }

        return null;
    }
}
