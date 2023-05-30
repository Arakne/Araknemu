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
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.simulation.effect;

import fr.arakne.utils.maps.BattlefieldCell;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;

import java.util.HashMap;
import java.util.Map;

/**
 * Compute boost value of application of a state
 * If the state is not registered, it'll be ignored
 */
public final class SetStateSimulator implements EffectSimulator {
    /**
     * Boost value of the state
     * Use negative for undesired state
     */
    private final Map<Integer, Integer> stateBoost;

    public SetStateSimulator() {
        this(new HashMap<>());
    }

    /**
     * @param stateBoost Mapping of boost value for each state
     */
    public SetStateSimulator(Map<Integer, Integer> stateBoost) {
        this.stateBoost = stateBoost;
    }

    /**
     * Define the boost value for a state
     *
     * @param state State id
     * @param boost Boost value. Negative for undesired boost
     *
     * @return this instance
     */
    public SetStateSimulator state(int state, int boost) {
        stateBoost.put(state, boost);

        return this;
    }

    @Override
    public void simulate(CastSimulation simulation, CastScope.EffectScope<? extends FighterData, ? extends BattlefieldCell> effect) {
        final Integer base = stateBoost.get(effect.effect().special());

        if (base == null) {
            return;
        }

        int duration = effect.effect().duration();

        if (duration == -1 || duration > 10) {
            duration = 10;
        }

        final int boost = base * Math.max(duration, 1);

        for (FighterData target : effect.targets()) {
            simulation.addBoost(boost, target);
        }
    }
}
