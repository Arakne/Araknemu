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

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.SpellEffectStub;
import fr.quatrevieux.araknemu.game.fight.ai.FighterAI;
import fr.quatrevieux.araknemu.game.fight.ai.action.logic.NullGenerator;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.FightBuff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.Damage;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
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

class PercentLifeLostDamageSimulatorTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter fighter;
    private Fighter target;
    private FighterAI ai;
    private PercentLifeLostDamageSimulator simulator;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fighter = player.fighter();
        target = other.fighter();
        target.init();
        fighter.init();
        target.life().alterMax(target, 1000);
        fighter.life().damage(target, 100);
        ai = new FighterAI(fighter, fight, new NullGenerator());
        simulator = new PercentLifeLostDamageSimulator(container.get(Simulator.class), Element.EARTH);
    }

    @Test
    void simulateSimple() {
        assertEquals(-10, simulate().enemiesLife());
        assertEquals(0, simulate().selfLife());

        fighter.life().heal(fighter, 50);
        assertEquals(-5, simulate().enemiesLife());
    }

    @Test
    void simulateWithArmorBuff() {
        target.buffs().add(new FightBuff(
            SpellEffectStub.fixed(105, 5),
            Mockito.mock(Spell.class),
            target,
            target,
            new BuffHook() {}
        ));

        assertEquals(-5, simulate().enemiesLife());
        assertEquals(0, simulate().selfLife());

        fighter.life().heal(fighter, 50);
        assertEquals(0, simulate().enemiesLife());
    }

    @Test
    void simulateWithCounterDamageCharacteristic() {
        target.characteristics().alter(Characteristic.COUNTER_DAMAGE, 10);

        assertEquals(-10, simulate().enemiesLife());
        assertEquals(-10, simulate().selfLife());
    }

    @Test
    void simulateWithCounterDamageBuff() {
        container.get(Simulator.class).registerBuff(999, new BuffEffectSimulator() {
            @Override
            public Damage onReduceableDamage(CastSimulation simulation, Buff buff, FighterData target, Damage damage) {
                return damage.reflect(10);
            }
        });

        target.buffs().add(
            new FightBuff(
                SpellEffectStub.fixed(999, 10),
                Mockito.mock(Spell.class),
                target,
                target,
                new BuffHook() {}
            )
        );

        assertEquals(-10, simulate().enemiesLife());
        assertEquals(-10, simulate().selfLife());
    }

    @Test
    void simulateWithResistance() {
        target.characteristics().alter(Characteristic.RESISTANCE_PERCENT_EARTH, 25);
        target.characteristics().alter(Characteristic.RESISTANCE_EARTH, 5);

        assertEquals(-2, simulate().enemiesLife());
        assertEquals(0, simulate().selfLife());

        fighter.life().damage(fighter, 100);
        assertEquals(-10, simulate().enemiesLife());

        simulator = new PercentLifeLostDamageSimulator(container.get(Simulator.class), Element.WATER);
        assertEquals(-20, simulate().enemiesLife());
    }

    @Test
    void simulateBuff() {
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

        CastScope<Fighter, FightCell> scope = makeCastScope(fighter, spell, effect, target.cell());
        simulator.simulate(simulation, ai, scope.effects().get(0));

        assertEquals(-15, simulation.enemiesLife());

        Mockito.when(effect.duration()).thenReturn(5);
        simulation = new CastSimulation(spell, fighter, target.cell());
        scope = makeCastScope(fighter, spell, effect, target.cell());
        simulator.simulate(simulation, ai, scope.effects().get(0));
        assertEquals(-37.5, simulation.enemiesLife());

        Mockito.when(effect.duration()).thenReturn(20);
        simulation = new CastSimulation(spell, fighter, target.cell());
        scope = makeCastScope(fighter, spell, effect, target.cell());
        simulator.simulate(simulation, ai, scope.effects().get(0));
        assertEquals(-75, simulation.enemiesLife());

        target.characteristics().alter(Characteristic.RESISTANCE_PERCENT_EARTH, 25);
        target.characteristics().alter(Characteristic.RESISTANCE_EARTH, 5);

        simulation = new CastSimulation(spell, fighter, target.cell());
        simulator.simulate(simulation, ai, scope.effects().get(0));
        assertEquals(-15, simulation.enemiesLife());
    }

    @Test
    void simulatePoisonShouldIgnoreArmorBuff() {
        target.buffs().add(new FightBuff(
            SpellEffectStub.fixed(105, 5),
            Mockito.mock(Spell.class),
            target,
            target,
            new BuffHook() {}
        ));

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

        CastScope<Fighter, FightCell> scope = makeCastScope(fighter, spell, effect, target.cell());
        simulator.simulate(simulation, ai, scope.effects().get(0));

        assertEquals(-15, simulation.enemiesLife());
    }

    @Test
    void simulateInfiniteBuff() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.duration()).thenReturn(-1);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastSimulation simulation = new CastSimulation(spell, fighter, target.cell());

        CastScope<Fighter, FightCell> scope = makeCastScope(fighter, spell, effect, target.cell());
        simulator.simulate(simulation, ai, scope.effects().get(0));

        assertEquals(-75, simulation.enemiesLife());
    }

    @Test
    void simulateArea() {
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

        assertEquals(-10, simulation.selfLife());
        assertEquals(-10, simulation.enemiesLife());
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

        CastScope<Fighter, FightCell> scope = makeCastScope(fighter, spell, effect, target.cell());
        simulator.simulate(simulation, ai, scope.effects().get(0));

        return simulation;
    }
}
