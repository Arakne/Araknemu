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
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.FightBuff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

/**
 * Buff effect for apply a heal when the fighter receive damage
 *
 * This effect is boosted by {@link Characteristic#INTELLIGENCE} and {@link Characteristic#HEALTH_BOOST}
 *
 * @see fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffListHooks#onLifeAltered(int)
 */
public final class HealOnDamageHandler implements EffectHandler, BuffHook {
    @Override
    public void handle(FightCastScope cast, FightCastScope.EffectScope effect) {
        // @fixme How to handle this case ? Used by spell 521
        for (Fighter target : effect.targets()) {
            apply(cast.caster(), effect.effect(), target);
        }
    }

    @Override
    public void buff(FightCastScope cast, FightCastScope.EffectScope effect) {
        for (Fighter target : effect.targets()) {
            target.buffs().add(new FightBuff(effect.effect(), cast.action(), cast.caster(), target, this));
        }
    }

    @Override
    public void onLifeAltered(FightBuff buff, int value) {
        if (value < 0) {
            apply(buff.caster(), buff.effect(), buff.target());
        }
    }

    private void apply(Fighter caster, SpellEffect effect, Fighter target) {
        final EffectValue value = EffectValue.create(effect, caster, target)
            .percent(caster.characteristics().get(Characteristic.INTELLIGENCE))
            .fixed(caster.characteristics().get(Characteristic.HEALTH_BOOST))
        ;

        target.life().heal(caster, value.value());
    }
}
