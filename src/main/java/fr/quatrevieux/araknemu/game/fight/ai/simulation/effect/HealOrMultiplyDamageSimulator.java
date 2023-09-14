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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.simulation.effect;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

/**
 * Simulate the "multiply or heal damage" effect
 *
 * The score depends on the average "healing" factor (efficiency), multiplied by duration
 *
 * So in average with :
 * - efficiency of 1 means no healing nor damage (e.g. damage is "reduced" by 100%)
 * - efficiency of 2 means healing of 100% of the damage (e.g. damage is "reduced" by 200%)
 * - efficiency of 0.5 means takes only 50% of the damage (e.g. damage is "reduced" by 50%)
 * - efficiency of 0 means takes 100% of the damage
 * - efficiency of -1 means takes 200% of the damage
 *
 * The efficiency is calculated as follows:
 * avgDamageFactor = [chance]% * [damage multiplier] # Simply multiply the chance to take damage by the damage multiplier
 * avgHealingFactor = (100% - [chance]%) * (1 + [healing multiplier]) # Reverse the chance to take damage and multiply the healing multiplier increased by 1 to take into account that damage is ignored even if the healing multiplier is 0
 * efficiency = avgHealingFactor - avgDamageFactor
 *
 * @see fr.quatrevieux.araknemu.game.fight.castable.effect.handler.armor.HealOrMultiplyDamageHandler
 */
public final class HealOrMultiplyDamageSimulator implements EffectSimulator {
    private final int score;

    /**
     * @param score The base score. Will be multiplied by the efficiency
     */
    public HealOrMultiplyDamageSimulator(int score) {
        this.score = score;
    }

    @Override
    public void simulate(CastSimulation simulation, AI ai, CastScope.EffectScope<? extends FighterData, ? extends BattlefieldCell> effect) {
        final SpellEffect spellEffect = effect.effect();
        final int chance = spellEffect.special();

        // Chance is in percent, so divide by 100
        final double efficiency = (((100d - chance) * (1d + spellEffect.max())) - (chance * spellEffect.min())) / 100;

        for (FighterData target : effect.targets()) {
            simulation.addBoost(efficiency * spellEffect.duration() * score, target);
        }
    }
}
