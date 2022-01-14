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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage;

import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterLife;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;

/**
 * Handle steal a fixed amount of like
 * Cannot be boosted nor reduced
 * This effect has no related element, and do not call buffs
 *
 * Applied damage are same as {@link FixedDamageHandler}
 */
public final class FixedStealLifeHandler implements EffectHandler {
    @Override
    public void handle(CastScope cast, CastScope.EffectScope effect) {
        final ActiveFighter caster = cast.caster();
        final FighterLife casterLife = caster.life();

        // This is a fixed effect, without any elements
        // So it does not call any buff hooks
        for (PassiveFighter target : effect.targets()) {
            casterLife.alter(caster, -target.life().alter(caster, -new EffectValue(effect.effect()).value()));
        }
    }

    @Override
    public void buff(CastScope cast, CastScope.EffectScope effect) {
        throw new UnsupportedOperationException("Fixed steal life effect do not supports buff");
    }
}
