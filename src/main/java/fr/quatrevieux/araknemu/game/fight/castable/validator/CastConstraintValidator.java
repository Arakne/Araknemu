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
import fr.quatrevieux.araknemu.game.fight.turn.Turn;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Validate a single castable constraint
 */
public interface CastConstraintValidator<C extends Castable> {
    /**
     * Check if the spell can be casted
     *
     * @param turn The current turn
     * @param castable The action to cast
     * @param target The target cell
     *
     * @return true if the cast is valid, or false if not
     */
    public boolean check(Turn turn, C castable, FightCell target);

    /**
     * Check if the spell can be casted
     *
     * @param turn The current turn
     * @param castable The action to cast
     * @param target The target cell
     *
     * @return The error if constraint failed, or null
     */
    public @Nullable Error validate(Turn turn, C castable, FightCell target);
}
