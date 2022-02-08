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

package fr.quatrevieux.araknemu.game.fight.ai.simulation.effect;

import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.Damage;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;

/**
 * Simulate simple damage effect
 *
 * @see fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.DamageHandler
 */
public final class DamageSimulator implements EffectSimulator {
    private final Element element;

    public DamageSimulator(Element element) {
        this.element = element;
    }

    @Override
    public void simulate(CastSimulation simulation, CastScope.EffectScope effect) {
        final ActiveFighter caster = simulation.caster();
        final int boost = caster.characteristics().get(element.boost());
        final int percent = caster.characteristics().get(Characteristic.PERCENT_DAMAGE);
        final int fixed = caster.characteristics().get(Characteristic.FIXED_DAMAGE);

        for (PassiveFighter target : effect.targets()) {
            final Interval value = EffectValue.create(effect.effect(), simulation.caster(), target)
                .percent(boost)
                .percent(percent)
                .fixed(fixed)
                .interval()
            ;

            final Interval damage = value.map(base -> computeDamage(base, target));

            if (effect.effect().duration() < 1) {
                simulation.addDamage(damage, target);
            } else {
                simulation.addPoison(damage, effect.effect().duration(), target);
            }
        }
    }

    private int computeDamage(int value, PassiveFighter target) {
        final Damage damage = new Damage(value, element)
            .percent(target.characteristics().get(element.percentResistance()))
            .fixed(target.characteristics().get(element.fixedResistance()))
        ;

        return damage.value();
    }
}
