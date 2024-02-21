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

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.FighterAI;
import fr.quatrevieux.araknemu.game.fight.ai.action.logic.NullGenerator;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InvokeDoubleSimulatorTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter fighter;
    private Fighter target;
    private FighterAI ai;
    private InvokeDoubleSimulator simulator;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fighter = player.fighter();
        target = other.fighter();

        fighter.init();
        target.init();

        fighter.life().damage(fighter, 40);
        target.life().alterMax(target, 500);
        ai = new FighterAI(fighter, fight, new NullGenerator());

        simulator = new InvokeDoubleSimulator();
    }

    @Test
    void simulateShouldOnlyFillInvocationScore() {
        CastSimulation simulation = simulate(195);

        assertEquals(250.0, simulation.invocation());
        assertEquals(0.0, simulation.enemiesLife());
        assertEquals(0.0, simulation.selfLife());
        assertEquals(0.0, simulation.alliesLife());
        assertEquals(0.0, simulation.selfBoost());
        assertEquals(0.0, simulation.alliesBoost());
        assertEquals(0.0, simulation.enemiesBoost());
    }

    @Test
    void simulateShouldPrioritizeCastToCellNearToEnemy() {
        assertEquals(250.0, simulate(195).invocation());
        assertEquals(251.0, simulate(181).invocation());
        assertEquals(252.0, simulate(167).invocation());
        assertEquals(253.0, simulate(153).invocation());
    }

    @Test
    void simulateWithHiddenEnemy() {
        target.setHidden(target, true);

        assertEquals(255.0, simulate(195).invocation());
        assertEquals(255.0, simulate(181).invocation());
        assertEquals(255.0, simulate(167).invocation());
        assertEquals(255.0, simulate(153).invocation());

        assertEquals(0.0, simulate(195).enemiesLife());
    }

    private CastSimulation simulate(int targetCell) {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastSimulation simulation = new CastSimulation(spell, fighter, fight.map().get(targetCell));

        CastScope<Fighter, FightCell> scope = makeCastScope(fighter, spell, effect, fight.map().get(targetCell));
        simulator.simulate(simulation, ai, scope.effects().get(0));

        return simulation;
    }
}