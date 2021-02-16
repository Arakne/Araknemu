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

import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;

/**
 * Simulator for simple alter characteristic effect
 */
public final class AlterCharacteristicSimulator implements EffectSimulator {
    private final int multiplier;

    /**
     * Creates without multiplier
     */
    public AlterCharacteristicSimulator() {
        this(1);
    }

    /**
     * Creates with defining a multiplier
     *
     * @param multiplier The value multiplier. Can be negative for malus buff
     */
    public AlterCharacteristicSimulator(int multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    public void simulate(CastSimulation simulation, CastScope.EffectScope effect) {
        final double value = (effect.effect().max() < effect.effect().min() ? effect.effect().min() : (double) (effect.effect().min() + effect.effect().max()) / 2)
            * multiplier
            * Math.max(effect.effect().duration(), 1)
        ;

        for (PassiveFighter target : effect.targets()) {
            simulation.addBoost(value, target);
        }
    }
}
