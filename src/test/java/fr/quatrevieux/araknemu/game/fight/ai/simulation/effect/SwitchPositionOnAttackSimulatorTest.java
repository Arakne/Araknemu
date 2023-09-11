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

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class SwitchPositionOnAttackSimulatorTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter fighter;
    private FighterAI ai;
    private SwitchPositionOnAttackSimulator simulator;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        simulator = new SwitchPositionOnAttackSimulator();
    }

    @Test
    void simulateShouldIgnoreSelf() {
        configure(fb ->
            fb
                .addSelf(fighter -> fighter.cell(168))
                .addEnemy(fighter -> fighter.cell(153))
        );

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
    void simulateSingleTarget() {
        configure(fb ->
            fb
                .addSelf(fighter -> fighter.cell(168).currentLife(100))
                .addAlly(fighter -> fighter.cell(170).currentLife(100))
                .addEnemy(fighter -> fighter.cell(153))
        );

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.duration()).thenReturn(2);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastSimulation simulation = new CastSimulation(spell, fighter, fight.map().get(170));

        CastScope<Fighter, FightCell> scope = makeCastScope(fighter, spell, effect, fight.map().get(170));
        simulator.simulate(simulation, ai, scope.effects().get(0));

        assertEquals(0, simulation.enemiesLife());
        assertEquals(0, simulation.alliesLife());
        assertEquals(0, simulation.selfLife());
        assertEquals(-200, simulation.selfBoost());
        assertEquals(200, simulation.alliesBoost());
        assertEquals(0, simulation.enemiesBoost());
    }

    @Test
    void simulateTargetHasLowerLife() {
        configure(fb ->
            fb
                .addSelf(fighter -> fighter.cell(168).currentLife(100))
                .addAlly(fighter -> fighter.cell(170).currentLife(50))
                .addEnemy(fighter -> fighter.cell(153))
        );

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.duration()).thenReturn(2);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastSimulation simulation = new CastSimulation(spell, fighter, fight.map().get(170));

        CastScope<Fighter, FightCell> scope = makeCastScope(fighter, spell, effect, fight.map().get(170));
        simulator.simulate(simulation, ai, scope.effects().get(0));

        assertEquals(0, simulation.enemiesLife());
        assertEquals(0, simulation.alliesLife());
        assertEquals(0, simulation.selfLife());
        assertEquals(-200, simulation.selfBoost());
        assertEquals(400, simulation.alliesBoost());
        assertEquals(0, simulation.enemiesBoost());
    }

    @Test
    void simulateTargetHasHigherLife() {
        configure(fb ->
            fb
                .addSelf(fighter -> fighter.cell(168).currentLife(100))
                .addAlly(fighter -> fighter.cell(170).currentLife(200).maxLife(200))
                .addEnemy(fighter -> fighter.cell(153))
        );

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.duration()).thenReturn(2);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastSimulation simulation = new CastSimulation(spell, fighter, fight.map().get(170));

        CastScope<Fighter, FightCell> scope = makeCastScope(fighter, spell, effect, fight.map().get(170));
        simulator.simulate(simulation, ai, scope.effects().get(0));

        assertEquals(0, simulation.enemiesLife());
        assertEquals(0, simulation.alliesLife());
        assertEquals(0, simulation.selfLife());
        assertEquals(-200, simulation.selfBoost());
        assertEquals(100, simulation.alliesBoost());
        assertEquals(0, simulation.enemiesBoost());
    }

    @Test
    void simulateWithArea() {
        configure(fb ->
            fb
                .addSelf(fighter -> fighter.cell(168).currentLife(100))
                .addAlly(fighter -> fighter.cell(249).currentLife(200).maxLife(200))
                .addAlly(fighter -> fighter.cell(263).currentLife(100))
                .addAlly(fighter -> fighter.cell(234).currentLife(50))
                .addEnemy(fighter -> fighter.cell(153))
        );

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.duration()).thenReturn(2);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 2)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastSimulation simulation = new CastSimulation(spell, fighter, fight.map().get(249));

        CastScope<Fighter, FightCell> scope = makeCastScope(fighter, spell, effect, fight.map().get(249));
        simulator.simulate(simulation, ai, scope.effects().get(0));

        assertEquals(0, simulation.enemiesLife());
        assertEquals(0, simulation.alliesLife());
        assertEquals(0, simulation.selfLife());
        assertEquals(-200, simulation.selfBoost());
        assertEquals(700, simulation.alliesBoost());
        assertEquals(0, simulation.enemiesBoost());
    }

    private void configure(Consumer<FightBuilder> configurator) {
        final FightBuilder builder = fightBuilder();
        configurator.accept(builder);

        fight = builder.build(true);
        fighter = player.fighter();
        ai = new FighterAI(fighter, fight, new NullGenerator());

        fight.nextState();
    }
}
