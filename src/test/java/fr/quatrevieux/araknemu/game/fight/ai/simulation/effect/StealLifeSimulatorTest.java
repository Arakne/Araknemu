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

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.area.CircleArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StealLifeSimulatorTest extends FightBaseCase {
    private Fight fight;
    private Fighter fighter;
    private Fighter target;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fighter = player.fighter();
        target = other.fighter();

        fighter.init();
        fighter.life().alter(fighter, -30);
    }

    @Test
    void simulateSimple() {
        assertEquals(-15, simulate().enemiesLife());
        assertEquals(7, simulate().selfLife());
    }

    @Test
    void simulateWithResistance() {
        target.characteristics().alter(Characteristic.RESISTANCE_EARTH, 5);
        assertEquals(-10, simulate().enemiesLife());
        assertEquals(5, simulate().selfLife());
    }

    @Test
    void simulateWithBoost() {
        fighter.characteristics().alter(Characteristic.STRENGTH, 100);
        assertEquals(-25, simulate().enemiesLife());
        assertEquals(12, simulate().selfLife());
    }

    @Test
    void simulateBuff() {
        StealLifeSimulator simulator = new StealLifeSimulator(Element.EARTH);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.duration()).thenReturn(2);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastSimulation simulation = new CastSimulation(spell, fighter, target.cell());

        CastScope<Fighter> scope = makeCastScope(fighter, spell, effect, target.cell());
        simulator.simulate(simulation, scope.effects().get(0));

        assertEquals(-22.5, simulation.enemiesLife());

        Mockito.when(effect.duration()).thenReturn(5);
        simulation = new CastSimulation(spell, fighter, target.cell());
        scope = makeCastScope(fighter, spell, effect, target.cell());
        simulator.simulate(simulation, scope.effects().get(0));
        assertEquals(-37.5, simulation.enemiesLife());

        Mockito.when(effect.duration()).thenReturn(-1);
        simulation = new CastSimulation(spell, fighter, target.cell());
        scope = makeCastScope(fighter, spell, effect, target.cell());
        simulator.simulate(simulation, scope.effects().get(0));
        assertEquals(-37.5, simulation.enemiesLife());
    }

    @Test
    void simulateArea() {
        StealLifeSimulator simulator = new StealLifeSimulator(Element.EARTH);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 10)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastSimulation simulation = new CastSimulation(spell, fighter, other.fighter().cell());

        CastScope<Fighter> scope = makeCastScope(fighter, spell, effect, other.fighter().cell());
        simulator.simulate(simulation, scope.effects().get(0));

        assertEquals(-8, simulation.selfLife());
        assertEquals(-15, simulation.enemiesLife());
    }

    private CastSimulation simulate() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastSimulation simulation = new CastSimulation(spell, fighter, target.cell());

        CastScope<Fighter> scope = makeCastScope(fighter, spell, effect, target.cell());
        new StealLifeSimulator(Element.EARTH).simulate(simulation, scope.effects().get(0));

        return simulation;
    }
}
