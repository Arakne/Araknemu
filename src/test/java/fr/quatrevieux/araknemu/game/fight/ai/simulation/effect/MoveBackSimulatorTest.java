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

class MoveBackSimulatorTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter fighter;
    private FighterAI ai;
    private MoveBackSimulator simulator;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fighter = player.fighter();
        ai = new FighterAI(fighter, fight, new NullGenerator());
        simulator = new MoveBackSimulator();
    }

    @Test
    void simulateNotBlockedShouldDoNothing() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastSimulation simulation = new CastSimulation(spell, fighter, fighter.cell());

        CastScope<Fighter, FightCell> scope = makeCastScope(fighter, spell, effect, fighter.cell());
        simulator.simulate(simulation, ai, scope.effects().get(0));

        assertEquals(0, simulation.enemiesLife());
        assertEquals(0, simulation.alliesLife());
        assertEquals(0, simulation.selfLife());
        assertEquals(0, simulation.selfBoost());
        assertEquals(0, simulation.alliesBoost());
        assertEquals(0, simulation.enemiesBoost());
    }

    @Test
    void simulateBlockedShouldComputeDamageDependingOfTheRemainingDistance() {
        fighter.move(fight.map().get(168));
        other.fighter().move(fight.map().get(182));
        other.properties().characteristics().base().set(Characteristic.VITALITY, 1000); // Ensure that the target will not die

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastSimulation simulation = new CastSimulation(spell, fighter, fighter.cell());

        CastScope<Fighter, FightCell> scope = makeCastScope(fighter, spell, effect, other.fighter().cell());
        simulator.simulate(simulation, ai, scope.effects().get(0));

        assertEquals(-37.5, simulation.enemiesLife()); // (9 + 16) / 2 * (5 - 2) * 50 / 50
        assertEquals(0, simulation.alliesLife());
        assertEquals(0, simulation.selfLife());
        assertEquals(0, simulation.selfBoost());
        assertEquals(0, simulation.alliesBoost());
        assertEquals(0, simulation.enemiesBoost());

        other.fighter().move(fight.map().get(196));
        simulation = new CastSimulation(spell, fighter, fighter.cell());
        scope = makeCastScope(fighter, spell, effect, other.fighter().cell());
        simulator.simulate(simulation, ai, scope.effects().get(0));
        assertEquals(-50.0, simulation.enemiesLife()); // (9 + 16) / 2 * (5 - 1) * 50 / 50

        other.fighter().move(fight.map().get(210));
        simulation = new CastSimulation(spell, fighter, fighter.cell());
        scope = makeCastScope(fighter, spell, effect, other.fighter().cell());
        simulator.simulate(simulation, ai, scope.effects().get(0));
        assertEquals(-62.5, simulation.enemiesLife()); // (9 + 16) / 2 * 5 * 50 / 50
    }

    @Test
    void simulateChainingDamage() {
        fight = fightBuilder()
            .addSelf(fb -> fb.cell(168))
            .addEnemy(fb -> fb.cell(153))
            .addEnemy(fb -> fb.cell(138))
            .addEnemy(fb -> fb.cell(123))
            .addEnemy(fb -> fb.cell(179)) // Ignore: not in line
            .build(true)
        ;

        fight.nextState();

        fighter = player.fighter();
        ai = new FighterAI(fighter, fight, new NullGenerator());

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(3);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastSimulation simulation = new CastSimulation(spell, fighter, fight.map().get(153));

        CastScope<Fighter, FightCell> scope = makeCastScope(fighter, spell, effect, fight.map().get(153));
        simulator.simulate(simulation, ai, scope.effects().get(0));

        assertEquals(-65, simulation.enemiesLife()); // 37.5 + 37.5 / 2 + 37.5 / 4
    }
}
