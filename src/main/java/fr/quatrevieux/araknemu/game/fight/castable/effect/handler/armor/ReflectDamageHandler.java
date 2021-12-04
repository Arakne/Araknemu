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
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.Damage;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;

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
    public void handle(CastScope cast, CastScope.EffectScope effect) {
        throw new UnsupportedOperationException("ReflectDamageHandler can only be used as buff");
    }

    @Override
    public void buff(CastScope cast, CastScope.EffectScope effect) {
        for (PassiveFighter target : effect.targets()) {
            target.buffs().add(new Buff(effect.effect(), cast.action(), cast.caster(), target, this));
        }
    }

    @Override
    public void onDirectDamage(Buff buff, ActiveFighter caster, Damage value) {
        // Ignore self damage
        if (!caster.equals(buff.target())) {
            value.reflect(
                new EffectValue(buff.effect())
                    .percent(buff.target().characteristics().get(Characteristic.WISDOM))
                    .value()
            );
        }
    }
}
