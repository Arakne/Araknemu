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
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.Damage;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.MultipliableDamage;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.ReflectedDamage;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.util.Asserter;

/**
 * Suffered damage will be healed, or multiplied
 *
 * Note: this buff is only applied on direct damage (see: https://forums.jeuxonline.info/sujet/969716/chance-d-ecaflip)
 */
public final class HealOrMultiplyDamageHandler implements EffectHandler, BuffHook {
    private final RandomUtil random = new RandomUtil();

    @Override
    public void handle(CastScope cast, CastScope.EffectScope effect) {
        throw new UnsupportedOperationException("HealOrMultiplyDamageHandler can only be used as buff");
    }

    @Override
    public void buff(CastScope cast, CastScope.EffectScope effect) {
        for (PassiveFighter target : effect.targets()) {
            target.buffs().add(new Buff(effect.effect(), cast.action(), cast.caster(), target, this));
        }
    }

    @Override
    public void onDirectDamage(Buff buff, ActiveFighter caster, Damage value) {
        apply(buff, value);
    }

    @Override
    public void onReflectedDamage(Buff buff, ReflectedDamage damage) {
        apply(buff, damage);
    }

    /**
     * Modify the damage multiplier depending on the chance
     */
    private void apply(Buff buff, MultipliableDamage damage) {
        final boolean heal = random.bool(Asserter.assertPercent(buff.effect().special()));

        if (heal) {
            damage.multiply(-buff.effect().max());
        } else {
            damage.multiply(buff.effect().min());
        }
    }
}
