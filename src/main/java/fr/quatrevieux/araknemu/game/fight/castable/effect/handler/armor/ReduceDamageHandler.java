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
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.armor;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.Damage;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;

/**
 * Handle reducing damages
 */
public final class ReduceDamageHandler implements EffectHandler, BuffHook {
    @Override
    public void handle(CastScope<Fighter> cast, CastScope<Fighter>.EffectScope effect) {
        throw new UnsupportedOperationException("ReduceDamageHandler can only be used as buff");
    }

    @Override
    public void buff(CastScope<Fighter> cast, CastScope<Fighter>.EffectScope effect) {
        for (Fighter target : effect.targets()) {
            target.buffs().add(new Buff(effect.effect(), cast.action(), cast.caster(), target, this));
        }
    }

    @Override
    public void onDirectDamage(Buff buff, Fighter caster, Damage value) {
        if (!supportsElement(buff, value.element())) {
            return;
        }

        final Characteristics characteristics = buff.target().characteristics();

        final int boost = 200 + characteristics.get(Characteristic.INTELLIGENCE) + characteristics.get(value.element().boost());
        final int reduce = buff.effect().min() * boost / 200;

        if (reduce < 0) {
            return;
        }

        value.reduce(reduce);
    }

    /**
     * Check if the armor supports the damage element
     *
     * @param buff The buff
     * @param element The damage element
     *
     * @return true if the armor supports the element
     */
    private boolean supportsElement(Buff buff, Element element) {
        return buff.effect().special() == 0 || Element.fromBitSet(buff.effect().special()).contains(element);
    }
}
