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

import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.SpellEffectStub;
import fr.quatrevieux.araknemu.game.fight.ai.FighterAI;
import fr.quatrevieux.araknemu.game.fight.ai.action.logic.NullGenerator;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.SpellScore;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.FightBuff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.Damage;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
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

import static org.junit.jupiter.api.Assertions.*;

class SpellReturnSimulatorTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter fighter;
    private FighterAI ai;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fighter = player.fighter();
        ai = new FighterAI(fighter, fight, new NullGenerator());
    }

    @ParameterizedTest
    @MethodSource("simpleCases")
    void simulateSimple(int level, int chance, int duration, double score) {
        SpellReturnSimulator simulator = new SpellReturnSimulator(50);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(level);
        Mockito.when(effect.special()).thenReturn(chance);
        Mockito.when(effect.duration()).thenReturn(duration);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastSimulation simulation = new CastSimulation(spell, fighter, fighter.cell());

        CastScope<Fighter, FightCell> scope = makeCastScope(fighter, spell, effect, fighter.cell());
        simulator.simulate(simulation, ai, scope.effects().get(0));

        assertEquals(score, simulation.selfBoost());
    }

    @ParameterizedTest
    @MethodSource("simpleCases")
    void scoreSimple(int level, int chance, int duration, double score) {
        SpellReturnSimulator simulator = new SpellReturnSimulator(50);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(level);
        Mockito.when(effect.special()).thenReturn(chance);
        Mockito.when(effect.duration()).thenReturn(duration);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        SpellScore spellScore = new SpellScore();
        simulator.score(spellScore, effect, fighter.characteristics());

        assertEquals((int) score, spellScore.boost());
    }

    public static Stream<Arguments> simpleCases() {
        return Stream.of(
            Arguments.arguments(5, 0, 5, 0.0),
            Arguments.arguments(1, 100, 5, 250.0),
            Arguments.arguments(2, 100, 5, 500.0),
            Arguments.arguments(3, 100, 5, 750.0),
            Arguments.arguments(4, 100, 5, 1000.0),
            Arguments.arguments(5, 100, 5, 1250.0),
            Arguments.arguments(5, 75, 5, 937.5),
            Arguments.arguments(5, 100, 0, 250.0),
            Arguments.arguments(5, 100, 1, 250.0),
            Arguments.arguments(5, 100, 3, 750.0),
            Arguments.arguments(5, 100, 20, 2500.0),
            Arguments.arguments(5, 100, -1, 2500.0)
        );
    }

    @Test
    void simulateArea() {
        SpellReturnSimulator simulator = new SpellReturnSimulator(50);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(3);
        Mockito.when(effect.special()).thenReturn(50);
        Mockito.when(effect.duration()).thenReturn(20);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 10)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastSimulation simulation = new CastSimulation(spell, fighter, other.fighter().cell());

        CastScope<Fighter, FightCell> scope = makeCastScope(fighter, spell, effect, other.fighter().cell());
        simulator.simulate(simulation, ai, scope.effects().get(0));

        assertEquals(750.0, simulation.selfBoost());
        assertEquals(750.0, simulation.enemiesBoost());
    }

    @Test
    void onReduceableDamageSuccess() {
        SpellReturnSimulator simulator = new SpellReturnSimulator(50);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(spell.level()).thenReturn(5);
        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastSimulation simulation = new CastSimulation(spell, fighter, fighter.cell());
        Damage damage = simulator.onReduceableDamage(
            simulation,
            new FightBuff(
                SpellEffectStub.fixed(100, 5).setSpecial(100),
                Mockito.mock(Spell.class),
                fighter,
                fighter,
                new BuffHook() {}
            ),
            fighter,
            new Damage(100, Element.NEUTRAL)
        );

        assertEquals(0, damage.value());
        assertEquals(100, damage.reflectedDamage());
    }

    @Test
    void onReduceableDamageLevelTooLow() {
        SpellReturnSimulator simulator = new SpellReturnSimulator(50);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(spell.level()).thenReturn(5);
        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastSimulation simulation = new CastSimulation(spell, fighter, fighter.cell());
        Damage damage = simulator.onReduceableDamage(
            simulation,
            new FightBuff(
                SpellEffectStub.fixed(100, 4).setSpecial(100),
                Mockito.mock(Spell.class),
                fighter,
                fighter,
                new BuffHook() {}
            ),
            fighter,
            new Damage(100, Element.NEUTRAL)
        );

        assertEquals(100, damage.value());
        assertEquals(0, damage.reflectedDamage());
    }

    @Test
    void onReduceableProbabilityTooLow() {
        SpellReturnSimulator simulator = new SpellReturnSimulator(50);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(spell.level()).thenReturn(5);
        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastSimulation simulation = new CastSimulation(spell, fighter, fighter.cell());
        Damage damage = simulator.onReduceableDamage(
            simulation,
            new FightBuff(
                SpellEffectStub.fixed(100, 5).setSpecial(50),
                Mockito.mock(Spell.class),
                fighter,
                fighter,
                new BuffHook() {}
            ),
            fighter,
            new Damage(100, Element.NEUTRAL)
        );

        assertEquals(100, damage.value());
        assertEquals(0, damage.reflectedDamage());
    }

    @Test
    void onReduceableDamageShouldIgnoreNegativeDamage() {
        SpellReturnSimulator simulator = new SpellReturnSimulator(50);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(spell.level()).thenReturn(5);
        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastSimulation simulation = new CastSimulation(spell, fighter, fighter.cell());
        Damage damage = simulator.onReduceableDamage(
            simulation,
            new FightBuff(
                SpellEffectStub.fixed(100, 5).setSpecial(100),
                Mockito.mock(Spell.class),
                fighter,
                fighter,
                new BuffHook() {}
            ),
            fighter,
            new Damage(100, Element.NEUTRAL).multiply(-1)
        );

        assertEquals(-100, damage.value());
        assertEquals(0, damage.reflectedDamage());
    }
}
