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
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.SpellScore;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.effect.util.Formula;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import fr.quatrevieux.araknemu.util.Asserter;

/**
 * Simulate fixed heal effect
 * This effect is not affected by boosts
 *
 * @see fr.quatrevieux.araknemu.game.fight.castable.effect.handler.heal.FixedHealHandler
 */
public final class FixedHealSimulator implements EffectSimulator {
    @Override
    public void simulate(CastSimulation simulation, AI ai, CastScope.EffectScope<? extends FighterData, ? extends BattlefieldCell> effect) {
        final SpellEffect spellEffect = effect.effect();
        final int duration = spellEffect.duration();
        final Interval value  = Interval.of(
            spellEffect.min(),
            Math.max(spellEffect.max(), spellEffect.min())
        );

        for (FighterData target : effect.targets()) {
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
        score.addHeal(Asserter.castNonNegative((int) Interval.of(effect.min(), Math.max(effect.max(), effect.min())).average()));
    }
}
