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

class ArmorSimulatorTest extends FightBaseCase {
    private Fight fight;
    private Fighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fighter = player.fighter();

        player.properties().characteristics().base().set(Characteristic.INTELLIGENCE, 0);
        player.properties().characteristics().base().set(Characteristic.STRENGTH, 0);
    }

    @Test
    void simulateShouldIgnoreNotBuff() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.duration()).thenReturn(0);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        assertEquals(0, performSimulation(spell, effect).selfBoost());
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

        assertEquals(100, performSimulation(spell, effect).selfBoost());

        Mockito.when(effect.duration()).thenReturn(3);
        assertEquals(150, performSimulation(spell, effect).selfBoost());
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
        Mockito.when(effect.duration()).thenReturn(1);

        CastSimulation simulation = performSimulation(spell, effect);

        assertEquals(50, simulation.selfBoost());
        assertEquals(50, simulation.enemiesBoost());
    }

    @Test
    void shouldOnlyConsiderCasterCharacteristics() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 10)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);
        Mockito.when(effect.duration()).thenReturn(1);

        fighter.characteristics().alter(Characteristic.INTELLIGENCE, 100);
        other.fighter().characteristics().alter(Characteristic.INTELLIGENCE, 100);
        CastSimulation simulation = performSimulation(spell, effect);

        assertEquals(80, simulation.selfBoost());
        assertEquals(50, simulation.enemiesBoost());
    }

    @Test
    void simulateShouldConsiderCasterIntelligence() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);
        Mockito.when(effect.duration()).thenReturn(1);

        assertEquals(50, performSimulation(spell, effect).selfBoost());

        fighter.characteristics().alter(Characteristic.INTELLIGENCE, 100);
        assertEquals(80, performSimulation(spell, effect).selfBoost());
    }

    @Test
    void simulateSingleElementArmorShouldConsiderCasterIntelligenceAndProtectedElement() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.special()).thenReturn(1 << Element.WATER.ordinal());
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);
        Mockito.when(effect.duration()).thenReturn(1);

        assertEquals(10, performSimulation(spell, effect).selfBoost());

        fighter.characteristics().alter(Characteristic.INTELLIGENCE, 100);
        assertEquals(15, performSimulation(spell, effect).selfBoost());

        fighter.characteristics().alter(Characteristic.LUCK, 100);
        assertEquals(20, performSimulation(spell, effect).selfBoost());
    }

    private CastSimulation performSimulation(Spell spell, SpellEffect effect) {
        ArmorSimulator simulator = new ArmorSimulator();

        CastSimulation simulation = new CastSimulation(spell, fighter, fighter.cell());

        CastScope scope = makeCastScope(fighter, spell, effect, fighter.cell());
        simulator.simulate(simulation, scope.effects().get(0));

        return simulation;
    }
}
