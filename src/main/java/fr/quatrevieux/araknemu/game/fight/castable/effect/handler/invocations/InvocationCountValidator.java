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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.invocations;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.castable.validator.CastConstraintValidator;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Check if the invoker can summon another creature
 * Should be used internally by invocation effect handlers
 */
public final class InvocationCountValidator implements CastConstraintValidator<Castable> {
    private final Fight fight;

    public InvocationCountValidator(Fight fight) {
        this.fight = fight;
    }

    private int max(FighterData fighter) {
        return fighter.characteristics().get(Characteristic.MAX_SUMMONED_CREATURES);
    }

    /**
     * Check if the invoker can summon another creature
     *
     * @param fighter the invoker
     *
     * @return true if the invoker can summon another creature, false otherwise
     */
    public boolean check(ActiveFighter fighter) {
        final int max = max(fighter);
        final int actual = (int) fight.turnList().fighters().stream() // Iterate only on active fighters (i.e. in turn list)
            .filter(other -> fighter.equals(other.invoker()))
            .count()
        ;

        return max > actual;
    }

    @Override
    public boolean check(Turn turn, Castable castable, BattlefieldCell target) {
        return check(turn.fighter());
    }

    @Override
    public @Nullable Error validate(Turn turn, Castable castable, BattlefieldCell target) {
        return check(turn, castable, target) ? null : Error.cantCastMaxSummonedCreaturesReached(max(turn.fighter()));
    }
}
