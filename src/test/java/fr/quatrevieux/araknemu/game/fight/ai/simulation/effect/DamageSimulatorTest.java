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
import fr.quatrevieux.araknemu.game.fight.SpellEffectStub;
import fr.quatrevieux.araknemu.game.fight.ai.FighterAI;
import fr.quatrevieux.araknemu.game.fight.ai.action.logic.NullGenerator;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.SpellScore;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.FightBuff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
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
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DamageSimulatorTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter fighter;
    private Fighter target;
    private FighterAI ai;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fighter = player.fighter();
        target = other.fighter();
        ai = new FighterAI(fighter, fight, new NullGenerator());
    }

    @Test
    void simulateSimple() {
        assertEquals(-15, simulate());
    }

    @Test
    void simulateWithResistance() {
        target.characteristics().alter(Characteristic.RESISTANCE_EARTH, 5);
        assertEquals(-10, simulate());

        target.characteristics().alter(Characteristic.RESISTANCE_PERCENT_EARTH, 25);
        assertEquals(-6, simulate());
    }

    @Test
    void simulateWithBoost() {
        fighter.characteristics().alter(Characteristic.FIXED_DAMAGE, 5);
        assertEquals(-20, simulate());

        fighter.characteristics().alter(Characteristic.PERCENT_DAMAGE, 25);
        assertEquals(-22, simulate());

        fighter.characteristics().alter(Characteristic.STRENGTH, 100);
        assertEquals(-32, simulate());
    }

    @Test
    void simulateBuff() {
        DamageSimulator simulator = new DamageSimulator(container.get(Simulator.class), Element.EARTH);

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

        CastScope<Fighter, FightCell> scope = makeCastScope(fighter, spell, effect, fighter.cell());
        simulator.simulate(simulation, ai, scope.effects().get(0));

        assertEquals(-22.5, simulation.selfLife());

        Mockito.when(effect.duration()).thenReturn(5);
        simulation = new CastSimulation(spell, fighter, fighter.cell());
        scope = makeCastScope(fighter, spell, effect, fighter.cell());
        simulator.simulate(simulation, ai, scope.effects().get(0));
        assertEquals(-56.25, simulation.selfLife());

        Mockito.when(effect.duration()).thenReturn(20);
        simulation = new CastSimulation(spell, fighter, fighter.cell());
        scope = makeCastScope(fighter, spell, effect, fighter.cell());
        simulator.simulate(simulation, ai, scope.effects().get(0));
        assertEquals(-112.5, simulation.selfLife());
    }

    @Test
    void simulateInfiniteBuff() {
        DamageSimulator simulator = new DamageSimulator(container.get(Simulator.class), Element.EARTH);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.duration()).thenReturn(-1);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastSimulation simulation = new CastSimulation(spell, fighter, fighter.cell());

        CastScope<Fighter, FightCell> scope = makeCastScope(fighter, spell, effect, fighter.cell());
        simulator.simulate(simulation, ai, scope.effects().get(0));

        assertEquals(-112.5, simulation.selfLife());
    }

    @Test
    void simulateArea() {
        DamageSimulator simulator = new DamageSimulator(container.get(Simulator.class), Element.EARTH);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 10)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastSimulation simulation = new CastSimulation(spell, fighter, other.fighter().cell());

        CastScope<Fighter, FightCell> scope = makeCastScope(fighter, spell, effect, other.fighter().cell());
        simulator.simulate(simulation, ai, scope.effects().get(0));

        assertEquals(-15, simulation.selfLife());
        assertEquals(-15, simulation.enemiesLife());
    }

    @Test
    void simulateWithCounterDamage() {
        other.fighter().characteristics().alter(Characteristic.COUNTER_DAMAGE, 5);

        CastSimulation simulation = doSimulation();
        assertEquals(-15, simulation.enemiesLife());
        assertEquals(-5, simulation.selfLife());
    }

    @Test
    void simulateWithReduceDamageBuff() {
        other.fighter().buffs().add(
            new FightBuff(
                SpellEffectStub.fixed(105, 5),
                Mockito.mock(Spell.class),
                fighter,
                other.fighter(),
                new BuffHook() {}
            )
        );

        assertEquals(-10, simulate());
    }

    @Test
    void simulateWithCounterDamageBuff() {
        other.fighter().buffs().add(
            new FightBuff(
                SpellEffectStub.fixed(107, 5),
                Mockito.mock(Spell.class),
                fighter,
                other.fighter(),
                new BuffHook() {}
            )
        );

        assertEquals(-15, doSimulation().enemiesLife());
        assertEquals(-5, doSimulation().selfLife());
    }

    @Test
    void simulateWithCounterDamageBuffNotFixedDamage() {
        other.fighter().buffs().add(
            new FightBuff(
                SpellEffectStub.fixed(107, 5),
                Mockito.mock(Spell.class),
                fighter,
                other.fighter(),
                new BuffHook() {}
            )
        );

        assertEquals(-18.5, doSimulation(10, 15).enemiesLife());
        assertEquals(-5, doSimulation(10, 15).selfLife());
    }

    @Test
    void simulateWithReturnSpell() {
        other.fighter().buffs().add(
            new FightBuff(
                SpellEffectStub.fixed(106, 5).setSpecial(100),
                Mockito.mock(Spell.class),
                fighter,
                other.fighter(),
                new BuffHook() {}
            )
        );

        assertEquals(0, doSimulation().enemiesLife());
        assertEquals(-15, doSimulation().selfLife());
    }

    @Test
    void score() {
        fighter.player().properties().characteristics().base().set(Characteristic.STRENGTH, 0);

        DamageSimulator simulator = new DamageSimulator(container.get(Simulator.class), Element.EARTH);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.max()).thenReturn(15);

        SpellScore score = new SpellScore();
        simulator.score(score, effect, fighter.characteristics());
        assertEquals(12, score.score());
        assertEquals(12, score.damage());

        fighter.characteristics().alter(Characteristic.STRENGTH, 100);

        score = new SpellScore();
        simulator.score(score, effect, fighter.characteristics());
        assertEquals(24, score.score());
        assertEquals(24, score.damage());
    }

    private double simulate() {
        return doSimulation().enemiesLife();
    }

    private CastSimulation doSimulation() {
        return doSimulation(10, 0);
    }

    private CastSimulation doSimulation(int min, int max) {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(min);
        Mockito.when(effect.max()).thenReturn(max);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastSimulation simulation = new CastSimulation(spell, fighter, target.cell());

        CastScope<Fighter, FightCell> scope = makeCastScope(fighter, spell, effect, target.cell());
        new DamageSimulator(container.get(Simulator.class), Element.EARTH).simulate(simulation, ai, scope.effects().get(0));

        return simulation;
    }
}
