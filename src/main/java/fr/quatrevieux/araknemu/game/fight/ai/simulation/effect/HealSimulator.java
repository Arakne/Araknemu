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
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.SpellScore;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.effect.util.Formula;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;

/**
 * Simulate simple heal effect
 *
 * @see fr.quatrevieux.araknemu.game.fight.castable.effect.handler.heal.HealHandler
 */
public final class HealSimulator implements EffectSimulator {
    @Override
    public void simulate(CastSimulation simulation, AI ai, CastScope.EffectScope<? extends FighterData, ? extends BattlefieldCell> effect) {
        final FighterData caster = simulation.caster();
        final int boost = caster.characteristics().get(Characteristic.INTELLIGENCE);
        final int fixed = caster.characteristics().get(Characteristic.HEALTH_BOOST);

        for (FighterData target : effect.targets()) {
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
                simulation.addHealBuff(value, Formula.capedDuration(effect.effect().duration()), target);
            }
        }
    }

    @Override
    public void score(SpellScore score, SpellEffect effect, Characteristics characteristics) {
        final int min = effect.min();
        final int max = Math.max(effect.max(), min);
        final int value = (min + max) / 2;
        final int boost = Math.max(100 + characteristics.get(Characteristic.INTELLIGENCE), 100);

        score.addHeal(value * boost / 100);
    }
}
