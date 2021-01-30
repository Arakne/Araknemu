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
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;

/**
 * Simulate steal life effect
 *
 * @see fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.StealLifeHandler
 */
final public class StealLifeSimulator implements EffectSimulator {
    final private DamageSimulator simulator;

    public StealLifeSimulator(Element element) {
        this.simulator = new DamageSimulator(element);
    }

    @Override
    public void simulate(CastSimulation simulation, CastScope.EffectScope effect) {
        final int lastDamage = -simulation.alliesLife() - simulation.enemiesLife();

        // Poison is already handled by the DamageSimulator
        simulator.simulate(simulation, effect);

        final int totalDamage = (-simulation.alliesLife() - simulation.enemiesLife()) - lastDamage;

        if (totalDamage > 0) {
            simulation.alterLife(totalDamage / 2, simulation.caster());
        }
    }
}
