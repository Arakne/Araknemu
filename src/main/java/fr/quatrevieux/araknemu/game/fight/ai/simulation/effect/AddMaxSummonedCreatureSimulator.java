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

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.effect.util.Formula;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;

/**
 * Simulation effect adding max summoned creatures
 *
 * Unlike base {@link AlterCharacteristicSimulator} simulator, this one takes in account current number of summoned creatures,
 * and ignore the effect if the max is not reached
 * Also, the duration of the effect is not used as multiplier, but simply added to the value
 *
 * @see fr.quatrevieux.araknemu.data.constant.Characteristic#MAX_SUMMONED_CREATURES
 */
public final class AddMaxSummonedCreatureSimulator implements EffectSimulator {
    private final int multiplier;

    /**
     * Creates with defining a multiplier
     *
     * @param multiplier The boost rate
     */
    public AddMaxSummonedCreatureSimulator(int multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    public void simulate(CastSimulation simulation, AI ai, CastScope.EffectScope<? extends FighterData, ? extends BattlefieldCell> effect) {
        final int duration = Formula.capedDuration(effect.effect().duration());

        final double value = (effect.effect().max() < effect.effect().min() ? effect.effect().min() : (double) (effect.effect().min() + effect.effect().max()) / 2)
            * multiplier
            + duration
        ;

        for (FighterData target : effect.targets()) {
            if (hasReachMax(target, ai)) {
                simulation.addBoost(value, target);
            }
        }
    }

    /**
     * Check if the target has reached the max number of summoned creatures
     *
     * @param target Fighter to check
     * @param ai AI instance
     *
     * @return true if the target has reached the max number of summoned creatures
     */
    private boolean hasReachMax(FighterData target, AI ai) {
        // @todo ignore static invocations
        final long currentCount = ai.fighters()
            .filter(fighter -> !fighter.dead())
            .filter(fighter -> target.equals(fighter.invoker()))
            .count()
        ;

        return currentCount >= target.characteristics().get(Characteristic.MAX_SUMMONED_CREATURES);
    }
}
