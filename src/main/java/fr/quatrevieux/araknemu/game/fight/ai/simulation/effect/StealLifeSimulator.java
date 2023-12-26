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

import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.SpellScore;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;

/**
 * Simulate steal life effect
 *
 * @see fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.StealLifeHandler
 */
public final class StealLifeSimulator implements EffectSimulator {
    private final DamageSimulator simulator;

    public StealLifeSimulator(Element element) {
        this.simulator = new DamageSimulator(element);
    }

    @Override
    public void simulate(CastSimulation simulation, AI ai, CastScope.EffectScope<? extends FighterData, ? extends BattlefieldCell> effect) {
        final double lastDamage = -simulation.alliesLife() - simulation.enemiesLife();

        // Poison is already handled by the DamageSimulator
        simulator.simulate(simulation, ai, effect);

        final double totalDamage = (-simulation.alliesLife() - simulation.enemiesLife()) - lastDamage;

        if (totalDamage > 0) {
            simulation.addHeal(Interval.of((int) totalDamage / 2), simulation.caster());
        }
    }

    @Override
    public void score(SpellScore score, SpellEffect effect, Characteristics characteristics) {
        // Only score the damage part
        simulator.score(score, effect, characteristics);
    }
}
