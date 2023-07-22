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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.shifting;

import fr.quatrevieux.araknemu.game.fight.castable.BaseCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;

/**
 * Handle the carry effect
 * If the spell caster or the target is already carried or carrying, the effect will be ignored
 *
 * Once carried, the target will be moved with the carrier,
 * and all spell effects will be applied to the carrier (except self target effects)
 *
 * This effect will be stopped if :
 * - one of the fighter die
 * - the carried is moved
 * - the carried is thrown using {@link ThrowHandler}
 *
 * This effect also apply a state to the carrier and the carried
 */
public final class CarryHandler implements EffectHandler {
    private final CarryingApplier applier;

    public CarryHandler(CarryingApplier applier) {
        this.applier = applier;
    }

    @Override
    public void buff(FightCastScope cast, BaseCastScope<Fighter, FightCell>.EffectScope effect) {
        throw new UnsupportedOperationException("Cannot use carry effect as buff");
    }

    @Override
    public void handle(FightCastScope cast, BaseCastScope<Fighter, FightCell>.EffectScope effect) {
        final Fighter caster = cast.caster();

        for (Fighter target : effect.targets()) {
            applier.carry(caster, target);
        }
    }
}
