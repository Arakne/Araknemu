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
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class InvokeMonsterSimulatorTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter fighter;
    private Fighter target;
    private FighterAI ai;
    private InvokeMonsterSimulator simulator;

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

        simulator = new InvokeMonsterSimulator(
            container.get(MonsterService.class),
            container.get(Simulator.class)
        );

        dataSet
            .pushFunctionalSpells()
            .pushMonsterTemplateInvocations()
            .pushMonsterSpellsInvocations()
        ;
    }

    @Test
    void simulateShouldComputeMonsterCharacteristics() {
        assertEquals(30.0, simulate(36, 1, 120).invocation());
        assertEquals(44.0, simulate(36, 2, 120).invocation());
        assertEquals(59.0, simulate(36, 3, 120).invocation());
        assertEquals(73.0, simulate(36, 4, 120).invocation());
        assertEquals(88.0, simulate(36, 5, 120).invocation());
        assertEquals(165.0, simulate(36, 6, 120).invocation());
    }

    @Test
    void simulateBlockingMonsterShouldOnlyComputeLifePoint() {
        assertEquals(40.0, simulate(115, 1, 125).invocation());
        assertEquals(50.0, simulate(115, 2, 125).invocation());
        assertEquals(60.0, simulate(115, 3, 125).invocation());
        assertEquals(70.0, simulate(115, 4, 125).invocation());
        assertEquals(80.0, simulate(115, 5, 125).invocation());
        assertEquals(90.0, simulate(115, 6, 125).invocation());
    }

    @Test
    void simulateShouldPrioritizeCastToCellNearToEnemy() {
        assertEquals(93.0, simulate(36, 5, 195).invocation());
        assertEquals(94.0, simulate(36, 5, 181).invocation());
        assertEquals(95.0, simulate(36, 5, 167).invocation());
        assertEquals(96.0, simulate(36, 5, 153).invocation());
    }

    @Test
    void simulateWithHiddenEnemyShouldIgnoreDistanceAndAttack() {
        target.setHidden(target, true);

        assertEquals(98.0, simulate(36, 5, 195).invocation());
        assertEquals(98.0, simulate(36, 5, 181).invocation());
        assertEquals(98.0, simulate(36, 5, 167).invocation());
        assertEquals(98.0, simulate(36, 5, 153).invocation());

        assertEquals(0.0, simulate(36, 5, 195).enemiesLife());
    }

    @Test
    void simulateShouldSetAttackValueWhenCanReachEnemy() {
        assertEquals(0.0, simulate(36, 5, 210).enemiesLife());
        assertEquals(-28.0, simulate(36, 5, 195).enemiesLife());
        assertEquals(0.0, simulate(36, 4, 195).enemiesLife());
        assertEquals(-23.0, simulate(36, 4, 181).enemiesLife());
    }

    @Test
    void suicideInvocationShouldComputeScoreOnlyIfReachEnemy() {
        assertEquals(1.0, simulate(116, 5, 120).invocation());
        // dmg * agi + life - distance = 35 * 2 + 25 - 4
        assertEquals(91.0, simulate(116, 5, 181).invocation());
        assertEquals(-70.0, simulate(116, 5, 181).enemiesLife());
    }

    @Test
    void shouldIgnoreSpellWithTooHighAp() {
        assertEquals(1199.0, simulate(158, 5, 139).invocation());
        assertEquals(0.0, simulate(158, 5, 139).enemiesLife());
    }

    @Test
    void shouldSimulateTheoreticalHeal() {
        assertEquals(936.0, simulate(39, 5, 120).invocation());
        assertEquals(18.0, simulate(39, 5, 120).selfLife());
        assertEquals(930.0, simulate(39, 5, 181).invocation()); // Lower score when near to enemy
    }

    private CastSimulation simulate(int monsterId, int level, int targetCell) {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(monsterId);
        Mockito.when(effect.max()).thenReturn(level);
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