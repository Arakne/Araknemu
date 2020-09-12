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

package fr.quatrevieux.araknemu.game.fight.ai.simulation.effect;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.Damage;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

/**
 * Simulate simple damage effect
 *
 * @see fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.DamageHandler
 */
final public class DamageSimulator implements EffectSimulator {
    final private Element element;

    public DamageSimulator(Element element) {
        this.element = element;
    }

    @Override
    public void simulate(CastSimulation simulation, CastScope.EffectScope effect) {
        int value = new EffectValue(effect.effect())
            .percent(simulation.caster().characteristics().get(element.boost()))
            .percent(simulation.caster().characteristics().get(Characteristic.PERCENT_DAMAGE))
            .fixed(simulation.caster().characteristics().get(Characteristic.FIXED_DAMAGE))
            .mean()
            .value()
        ;

        for (PassiveFighter target : effect.targets()) {
            Damage damage = new Damage(value, element)
                .percent(target.characteristics().get(element.percentResistance()))
                .fixed(target.characteristics().get(element.fixedResistance()))
            ;

            simulation.addDamage(Math.max(applyBuff(damage.value(), effect.effect()), 1), target);
        }
    }

    /**
     * Modify the damage value by applying effect duration (poison)
     *
     * @param baseValue The base damage value
     * @param effect The spell effect
     */
    private int applyBuff(int baseValue, SpellEffect effect) {
        if (effect.duration() < 1) {
            return baseValue;
        }

        return (int) (baseValue * effect.duration() * 0.75);
    }
}
