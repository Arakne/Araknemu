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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.heal;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

/**
 * Buff effect for apply a heal when the fighter receive damage
 *
 * This effect is boosted by {@link Characteristic#INTELLIGENCE} and {@link Characteristic#HEALTH_BOOST}
 *
 * @see fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buffs#onLifeAltered(int)
 */
public final class HealOnDamageHandler implements EffectHandler, BuffHook {
    @Override
    public void handle(CastScope cast, CastScope.EffectScope effect) {
        // @fixme How to handle this case ? Used by spell 521
        for (PassiveFighter target : effect.targets()) {
            apply(cast.caster(), effect.effect(), target);
        }
    }

    @Override
    public void buff(CastScope cast, CastScope.EffectScope effect) {
        for (PassiveFighter target : effect.targets()) {
            target.buffs().add(new Buff(effect.effect(), cast.action(), cast.caster(), target, this));
        }
    }

    @Override
    public void onLifeAltered(Buff buff, int value) {
        if (value < 0) {
            apply(buff.caster(), buff.effect(), buff.target());
        }
    }

    private void apply(ActiveFighter caster, SpellEffect effect, PassiveFighter target) {
        final EffectValue value = EffectValue.create(effect, caster, target)
            .percent(caster.characteristics().get(Characteristic.INTELLIGENCE))
            .fixed(caster.characteristics().get(Characteristic.HEALTH_BOOST))
        ;

        target.life().alter(caster, value.value());
    }
}
