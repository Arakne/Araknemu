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

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.effect.util.Formula;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;

/**
 * Simulator for add characteristic when character takes damage effect
 * The score is computed by the formula: `score = max * multiplier * duration`
 *
 * Note: the score do not depend on the boosted characteristic (min effect parameter)
 *
 * @see fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.AddCharacteristicOnDamageHandler The simulated effect
 */
public final class AddCharacteristicOnDamageSimulator implements EffectSimulator {
    private final double multiplier;

    /**
     * @param multiplier The value multiplier
     */
    public AddCharacteristicOnDamageSimulator(double multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    public void simulate(CastSimulation simulation, AI ai, CastScope.EffectScope<? extends FighterData, ? extends BattlefieldCell> effect) {
        final int duration = Formula.capedDuration(effect.effect().duration());
        final double value = effect.effect().max() * multiplier * duration;

        for (FighterData target : effect.targets()) {
            simulation.addBoost(value, target);
        }
    }
}
