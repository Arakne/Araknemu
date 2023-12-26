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
import fr.quatrevieux.araknemu.game.spell.effect.area.CircleArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DamageOnActionPointUseSimulatorTest extends FightBaseCase {
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
        target.init();
        target.life().alterMax(target, 1000);
        ai = new FighterAI(fighter, fight, new NullGenerator());
    }

    @Test
    void simulateSimple() {
        assertEquals(-54.0, simulate());
    }

    @Test
    void simulateWithResistance() {
        target.characteristics().alter(Characteristic.RESISTANCE_FIRE, 5);
        assertEquals(-42.75, simulate());

        target.characteristics().alter(Characteristic.RESISTANCE_PERCENT_FIRE, 25);
        assertEquals(-29.25, simulate());
    }

    @Test
    void simulateWithBoost() {
        fighter.characteristics().alter(Characteristic.FIXED_DAMAGE, 5);
        assertEquals(-65.25, simulate());

        fighter.characteristics().alter(Characteristic.PERCENT_DAMAGE, 25);
        assertEquals(-78.75, simulate());

        fighter.characteristics().alter(Characteristic.INTELLIGENCE, 100);
        assertEquals(-105.75, simulate());
    }

    @Test
    void simulateBuff() {
        assertEquals(-18, simulateWithDuration(1));
        assertEquals(-36, simulateWithDuration(2));
        assertEquals(-54, simulateWithDuration(3));
        assertEquals(-126, simulateWithDuration(7));
        assertEquals(-180, simulateWithDuration(10));
        assertEquals(-180, simulateWithDuration(100));
        assertEquals(-180, simulateWithDuration(-1));
    }

    @Test
    void simulateDifferentParameters() {
        assertEquals(-18, simulate(1, 1, 2));
        assertEquals(-36, simulate(1, 2, 2));
        assertEquals(-18, simulate(2, 2, 2));
        assertEquals(-4.5, simulate(4, 1, 2));
    }

    @Test
    void simulateArea() {
        DamageOnActionPointUseSimulator simulator = new DamageOnActionPointUseSimulator();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(1);
        Mockito.when(effect.max()).thenReturn(1);
        Mockito.when(effect.duration()).thenReturn(2);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 10)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastSimulation simulation = new CastSimulation(spell, fighter, other.fighter().cell());

        CastScope<Fighter, FightCell> scope = makeCastScope(fighter, spell, effect, other.fighter().cell());
        simulator.simulate(simulation, ai, scope.effects().get(0));

        assertEquals(-18, simulation.selfLife());
        assertEquals(-18, simulation.enemiesLife());
    }

    private double simulateWithDuration(int duration) {
        return simulate(1, 2, duration);
    }

    private double simulate() {
        return simulate(1, 2, 3);
    }

    private double simulate(int min, int max, int duration) {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(min);
        Mockito.when(effect.max()).thenReturn(max);
        Mockito.when(effect.duration()).thenReturn(duration);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastSimulation simulation = new CastSimulation(spell, fighter, target.cell());

        CastScope<Fighter, FightCell> scope = makeCastScope(fighter, spell, effect, target.cell());
        new DamageOnActionPointUseSimulator().simulate(simulation, ai, scope.effects().get(0));

        return simulation.enemiesLife();
    }
}
