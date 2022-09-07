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

import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.area.CircleArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AlterActionPointsSimulatorTest extends FightBaseCase {
    private Fight fight;
    private Fighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fighter = player.fighter();
    }

    @Test
    void simulateOnCurrentTurn() {
        AlterActionPointsSimulator simulator = new AlterActionPointsSimulator(200);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(spell.apCost()).thenReturn(5);
        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastSimulation simulation = new CastSimulation(spell, fighter, fighter.cell());

        CastScope scope = makeCastScope(fighter, spell, effect, fighter.cell());
        simulator.simulate(simulation, scope.effects().get(0));

        assertEquals(1000, simulation.selfBoost());
        assertEquals(0.1, simulation.actionPointsCost());
    }

    @Test
    void simulateNegativeCurrentTurn() {
        AlterActionPointsSimulator simulator = new AlterActionPointsSimulator(-200);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(spell.apCost()).thenReturn(5);
        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastSimulation simulation = new CastSimulation(spell, fighter, fighter.cell());

        CastScope scope = makeCastScope(fighter, spell, effect, fighter.cell());
        simulator.simulate(simulation, scope.effects().get(0));

        assertEquals(0, simulation.selfBoost());
        assertEquals(15, simulation.actionPointsCost());
    }

    @Test
    void simulateBuff() {
        AlterActionPointsSimulator simulator = new AlterActionPointsSimulator(200);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.duration()).thenReturn(2);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastSimulation simulation = new CastSimulation(spell, fighter, fighter.cell());

        CastScope scope = makeCastScope(fighter, spell, effect, fighter.cell());
        simulator.simulate(simulation, scope.effects().get(0));

        assertEquals(0.1, simulation.actionPointsCost());
        assertEquals(4000, simulation.selfBoost());

        Mockito.when(effect.duration()).thenReturn(5);
        simulation = new CastSimulation(spell, fighter, fighter.cell());
        scope = makeCastScope(fighter, spell, effect, fighter.cell());
        simulator.simulate(simulation, scope.effects().get(0));
        assertEquals(0.1, simulation.actionPointsCost());
        assertEquals(10000, simulation.selfBoost());

        Mockito.when(effect.duration()).thenReturn(20);
        simulation = new CastSimulation(spell, fighter, fighter.cell());
        scope = makeCastScope(fighter, spell, effect, fighter.cell());
        simulator.simulate(simulation, scope.effects().get(0));
        assertEquals(0.1, simulation.actionPointsCost());
        assertEquals(20000, simulation.selfBoost());

        Mockito.when(effect.duration()).thenReturn(-1);
        simulation = new CastSimulation(spell, fighter, fighter.cell());
        scope = makeCastScope(fighter, spell, effect, fighter.cell());
        simulator.simulate(simulation, scope.effects().get(0));
        assertEquals(0.1, simulation.actionPointsCost());
        assertEquals(20000, simulation.selfBoost());
    }

    @Test
    void simulateArea() {
        AlterActionPointsSimulator simulator = new AlterActionPointsSimulator(200);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(spell.apCost()).thenReturn(2);
        Mockito.when(effect.min()).thenReturn(1);
        Mockito.when(effect.duration()).thenReturn(1);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 10)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastSimulation simulation = new CastSimulation(spell, fighter, other.fighter().cell());

        CastScope scope = makeCastScope(fighter, spell, effect, other.fighter().cell());
        simulator.simulate(simulation, scope.effects().get(0));

        assertEquals(1, simulation.actionPointsCost());
        assertEquals(200, simulation.selfBoost());
        assertEquals(200, simulation.enemiesBoost());
    }

    @ParameterizedTest
    @MethodSource("provideCurrentTurnEffects")
    void onCurrentTurnShouldAlterSpellAPCost(int effectValue, double expectedBoost, double expectedApCost) {
        AlterActionPointsSimulator simulator = new AlterActionPointsSimulator(200);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(spell.apCost()).thenReturn(5);
        Mockito.when(effect.min()).thenReturn(effectValue);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastSimulation simulation = new CastSimulation(spell, fighter, fighter.cell());

        CastScope scope = makeCastScope(fighter, spell, effect, fighter.cell());
        simulator.simulate(simulation, scope.effects().get(0));

        assertEquals(expectedBoost, simulation.selfBoost());
        assertEquals(expectedApCost, simulation.actionPointsCost());
    }

    public static Stream<Arguments> provideCurrentTurnEffects() {
        return Stream.of(
            Arguments.of(1, 0.0, 4.0),
            Arguments.of(2, 0.0, 3.0),
            Arguments.of(3, 0.0, 2.0),
            Arguments.of(4, 0.0, 1.0),
            Arguments.of(5, 0.0, 0.1),
            Arguments.of(6, 200.0, 0.1),
            Arguments.of(7, 400.0, 0.1)
        );
    }

    @ParameterizedTest
    @MethodSource("provideCurrentNegativeTurnEffects")
    void onCurrentTurnWithNegativeEffect(int effectValue, double expectedApCost) {
        AlterActionPointsSimulator simulator = new AlterActionPointsSimulator(-200);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(spell.apCost()).thenReturn(5);
        Mockito.when(effect.min()).thenReturn(effectValue);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastSimulation simulation = new CastSimulation(spell, fighter, fighter.cell());

        CastScope scope = makeCastScope(fighter, spell, effect, fighter.cell());
        simulator.simulate(simulation, scope.effects().get(0));

        assertEquals(0.0, simulation.selfBoost());
        assertEquals(expectedApCost, simulation.actionPointsCost());
    }

    public static Stream<Arguments> provideCurrentNegativeTurnEffects() {
        return Stream.of(
            Arguments.of(1, 6.0),
            Arguments.of(2, 7.0),
            Arguments.of(10, 15.0)
        );
    }
}
