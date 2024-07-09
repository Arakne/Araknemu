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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.armor;

import fr.arakne.utils.value.helper.RandomUtil;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.FightBuff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.Damage;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.MultipliableDamage;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.ReflectedDamage;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.util.Asserter;

/**
 * Suffered damage will be healed, or multiplied
 *
 * Parameters:
 * - min: multiplier if damage is not healed
 * - max: multiplier if damage is healed
 * - special: chance to take damage instead of healing
 *
 * Note: this buff is only applied on direct damage (see: https://forums.jeuxonline.info/sujet/969716/chance-d-ecaflip)
 */
public final class HealOrMultiplyDamageHandler implements EffectHandler, BuffHook {
    private final RandomUtil random = new RandomUtil();

    @Override
    public void handle(FightCastScope cast, FightCastScope.EffectScope effect) {
        throw new UnsupportedOperationException("HealOrMultiplyDamageHandler can only be used as buff");
    }

    @Override
    public void buff(FightCastScope cast, FightCastScope.EffectScope effect) {
        for (Fighter target : effect.targets()) {
            target.buffs().add(new FightBuff(effect.effect(), cast.action(), cast.caster(), target, this));
        }
    }

    @Override
    public void onDirectDamage(FightBuff buff, Fighter caster, Damage value) {
        apply(buff, value);
    }

    @Override
    public void onReflectedDamage(FightBuff buff, ReflectedDamage damage) {
        apply(buff, damage);
    }

    /**
     * Modify the damage multiplier depending on the chance
     */
    private void apply(FightBuff buff, MultipliableDamage damage) {
        final boolean heal = !random.bool(Asserter.assertPercent(buff.effect().special()));

        if (heal) {
            damage.multiply(-buff.effect().max());
        } else {
            damage.multiply(buff.effect().min());
        }
    }
}
