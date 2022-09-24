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
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;

/**
 * Handle fixed damage, on the spell caster
 * Cannot be boosted nor reduced
 * This effect has no related element, and do not call buffs
 *
 * This effect is same as {@link FixedDamageHandler} when applied only on the caster
 */
public final class FixedCasterDamageHandler implements EffectHandler {
    @Override
    public void handle(CastScope<Fighter> cast, CastScope<Fighter>.EffectScope effect) {
        final PassiveFighter caster = cast.caster();

        // This is a fixed effect, without any elements
        // So it does not call any buff hooks
        caster.life().alter(caster, -EffectValue.create(effect.effect(), caster, caster).value());
    }

    @Override
    public void buff(CastScope<Fighter> cast, CastScope<Fighter>.EffectScope effect) {
        throw new UnsupportedOperationException("Fixed caster damage effect do not supports buff");
    }
}
