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

package fr.quatrevieux.araknemu.game.fight.ai.simulation;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.factory.ChainAiFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.module.AiModule;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimulatorTest extends FightBaseCase {
    private Simulator simulator;
    private Fight fight;
    private PlayerFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushFunctionalSpells();

        fight = createFight();
        fighter = player.fighter();
        fight.register(new AiModule(new ChainAiFactory()));
        simulator = container.get(Simulator.class);
    }

    @Test
    void simulateWithoutSupportedEffects() {
        simulator = new Simulator();

        CastSimulation simulation = simulator.simulate(fighter.spells().get(3), fighter, other.fighter().cell());

        assertEquals(0, simulation.alliesLife());
        assertEquals(0, simulation.enemiesLife());
        assertEquals(0, simulation.selfLife());
        assertEquals(0, simulation.alliesBoost());
        assertEquals(0, simulation.enemiesBoost());
        assertEquals(0, simulation.selfBoost());

        assertEquals(fighter.spells().get(3), simulation.spell());
        assertEquals(fighter, simulation.caster());
        assertEquals(other.fighter().cell(), simulation.target());
    }

    @Test
    void simulateAttack() {
        CastSimulation simulation = simulator.simulate(fighter.spells().get(3), fighter, other.fighter().cell());

        assertEquals(0, simulation.alliesLife());
        assertEquals(-10.2, simulation.enemiesLife(), 0.1);
        assertEquals(0, simulation.selfLife());
        assertEquals(0, simulation.alliesBoost());
        assertEquals(0, simulation.enemiesBoost());
        assertEquals(0, simulation.selfBoost());
    }

    @Test
    void simulateAttackShouldLimitByTargetLife() {
        fighter.characteristics().alter(Characteristic.FIXED_DAMAGE, 1000);

        CastSimulation simulation = simulator.simulate(fighter.spells().get(3), fighter, other.fighter().cell());

        assertEquals(-50, simulation.enemiesLife());
    }

    @Test
    void simulateBoost() {
        CastSimulation simulation = simulator.simulate(getSpell(42, 1), fighter, fighter.cell());

        assertEquals(0, simulation.alliesLife());
        assertEquals(0, simulation.enemiesLife());
        assertEquals(0, simulation.selfLife());
        assertEquals(0, simulation.alliesBoost());
        assertEquals(0, simulation.enemiesBoost());
        assertEquals(79, simulation.selfBoost(), .1);
    }

    @Test
    void simulateWithProbableEffects() {
        CastSimulation simulation = simulator.simulate(getSpell(109, 5), fighter, other.fighter().cell());

        assertEquals(0, simulation.alliesLife());
        assertEquals(-23.5, simulation.enemiesLife(), 0.1);
        assertEquals(0, simulation.selfLife());
        assertEquals(0, simulation.alliesBoost());
        assertEquals(0, simulation.enemiesBoost());
        assertEquals(0, simulation.selfBoost());
    }

    @Test
    void simulateShouldHandleCriticality() {
        assertEquals(-23.5, simulator.simulate(getSpell(109, 5), fighter, other.fighter().cell()).enemiesLife(), 0.1);

        fighter.characteristics().alter(Characteristic.CRITICAL_BONUS, 100);
        assertEquals(-36.5, simulator.simulate(getSpell(109, 5), fighter, other.fighter().cell()).enemiesLife(), 0.1);
    }

    @Test
    void simulatePoison() {
        CastSimulation simulation = simulator.simulate(getSpell(181, 5), fighter, other.fighter().cell());

        assertEquals(0, simulation.alliesLife());
        assertEquals(-36.03, simulation.enemiesLife(), 0.1);
        assertEquals(-36.8, simulation.selfLife(), 0.1);
        assertEquals(0, simulation.alliesBoost());
        assertEquals(0, simulation.enemiesBoost());
        assertEquals(0, simulation.selfBoost());
    }

    @Test
    void simulateWithoutCriticalEffects() {
        assertEquals(-2000, simulator.simulate(getSpell(468, 5), fighter, other.fighter().cell()).enemiesBoost());
    }

    private Spell getSpell(int id, int level) {
        return container.get(SpellService.class).get(id).level(level);
    }
}
