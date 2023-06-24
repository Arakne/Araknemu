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

package fr.quatrevieux.araknemu.game.fight.castable.validator;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Validate the cast by delegating validation to the current action effect handlers
 *
 * @see fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler
 * @see Fight#effects()
 * @see fr.quatrevieux.araknemu.game.fight.castable.effect.EffectsHandler#check(Turn, Castable, BattlefieldCell)
 * @see fr.quatrevieux.araknemu.game.fight.castable.effect.EffectsHandler#validate(Turn, Castable, BattlefieldCell)
 */
public final class EffectHandlersValidator implements CastConstraintValidator<Castable> {
    private final Fight fight;

    public EffectHandlersValidator(Fight fight) {
        this.fight = fight;
    }

    @Override
    public boolean check(Turn turn, Castable castable, BattlefieldCell target) {
        return fight.effects().check(turn, castable, target);
    }

    @Override
    public @Nullable Object validate(Turn turn, Castable castable, BattlefieldCell target) {
        return fight.effects().validate(turn, castable, target);
    }
}
