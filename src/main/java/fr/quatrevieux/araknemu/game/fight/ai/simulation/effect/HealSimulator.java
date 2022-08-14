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
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

/**
 * Simulate simple heal effect
 *
 * @see fr.quatrevieux.araknemu.game.fight.castable.effect.handler.heal.HealHandler
 */
public final class HealSimulator implements EffectSimulator {
    @Override
    public void simulate(CastSimulation simulation, CastScope.EffectScope effect) {
        final ActiveFighter caster = simulation.caster();
        final int boost = caster.characteristics().get(Characteristic.INTELLIGENCE);
        final int fixed = caster.characteristics().get(Characteristic.HEALTH_BOOST);

        for (PassiveFighter target : effect.targets()) {
            final SpellEffect spellEffect = effect.effect();
            final Interval value = EffectValue.create(spellEffect, simulation.caster(), target)
                .percent(boost)
                .fixed(fixed)
                .interval()
            ;

            final int duration = spellEffect.duration();

            if (duration == 0) {
                simulation.addHeal(value, target);
            } else {
                // Limit duration to 10
                simulation.addHealBuff(value, duration < 1 || duration > 10 ? 10 : duration, target);
            }
        }
    }
}
