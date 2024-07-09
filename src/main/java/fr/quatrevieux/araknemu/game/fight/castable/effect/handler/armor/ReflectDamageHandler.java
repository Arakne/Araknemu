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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.armor;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.FightBuff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.Damage;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

/**
 * Reflect suffered damage
 * Unlike item effect, this one is modified by wisdom
 *
 * Reflected damage cannot exceed half of cast damage, and is lowered by resistance
 *
 * @see fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.ReflectedDamage For the formula
 */
public final class ReflectDamageHandler implements EffectHandler, BuffHook {
    @Override
    public void handle(FightCastScope cast, FightCastScope.EffectScope effect) {
        throw new UnsupportedOperationException("ReflectDamageHandler can only be used as buff");
    }

    @Override
    public void buff(FightCastScope cast, FightCastScope.EffectScope effect) {
        for (Fighter target : effect.targets()) {
            target.buffs().add(new FightBuff(effect.effect(), cast.action(), cast.caster(), target, this));
        }
    }

    @Override
    public void onDirectDamage(FightBuff buff, Fighter caster, Damage value) {
        // Ignore self damage
        if (!caster.equals(buff.target())) {
            value.reflect(
                EffectValue.create(buff.effect(), buff.target(), caster)
                    .percent(buff.target().characteristics().get(Characteristic.WISDOM))
                    .value()
            );
        }
    }
}
