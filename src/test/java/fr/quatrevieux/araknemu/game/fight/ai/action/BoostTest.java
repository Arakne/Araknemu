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

package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.ai.AiBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.action.util.CastSpell;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.fighter.invocation.DoubleFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.invocation.InvocationFighter;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import fr.quatrevieux.araknemu.game.spell.Spell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BoostTest extends AiBaseCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushFunctionalSpells();
    }

    @Test
    void success() {
        action = Boost.self(container.get(Simulator.class));

        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(126, 5))
            .addEnemy(builder -> builder.cell(125))
        );

        assertCast(126, 122);

        assertEquals(126, ai.get(CastSpell.LAST_CAST).spell().id());
        assertEquals(122, ai.get(CastSpell.LAST_CAST).target().id());
    }

    @Test
    void successShouldBoostMainAlly() throws SQLException {
        dataSet
            .pushMonsterTemplateInvocations()
            .pushMonsterSpellsInvocations()
        ;
        action = Boost.self(container.get(Simulator.class));

        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122))
            .addAlly(builder -> builder.cell(127))
            .addEnemy(builder -> builder.cell(125))
        );

        InvocationFighter invoc = new InvocationFighter(
            -10,
            container.get(MonsterService.class).load(39).get(5), // Lapino
            player.fighter().team(),
            player.fighter()
        );
        fight.fighters().joinTurnList(invoc, fight.map().get(126));
        configureFighterAi(invoc);

        assertCast(582, 122);

        assertEquals(582, ai.get(CastSpell.LAST_CAST).spell().id());
        assertEquals(122, ai.get(CastSpell.LAST_CAST).target().id());
    }

    @Test
    void successWithAllEnemiesInvisible() {
        action = Boost.self(container.get(Simulator.class));

        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(126, 5))
            .addEnemy(builder -> builder.player(other).cell(125))
        );

        other.fighter().setHidden(other.fighter(), true);

        assertCast(126, 122);

        assertEquals(126, ai.get(CastSpell.LAST_CAST).spell().id());
        assertEquals(122, ai.get(CastSpell.LAST_CAST).target().id());
    }

    @Test
    void notEnoughAP() {
        action = Boost.self(container.get(Simulator.class));

        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(126, 5))
            .addEnemy(builder -> builder.cell(125))
        );

        setAP(1);

        assertDotNotGenerateAction();
        assertNull(ai.get(CastSpell.LAST_CAST));
    }

    @Test
    void notAP() {
        action = Boost.self(container.get(Simulator.class));

        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(126, 5))
            .addEnemy(builder -> builder.cell(125))
        );
        removeAllAP();

        assertDotNotGenerateAction();
        assertNull(ai.get(CastSpell.LAST_CAST));
    }

    @Test
    void withAreaSpell() {
        action = Boost.self(container.get(Simulator.class));

        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(584, 5))
            .addAlly(builder -> builder.cell(78))
            .addAlly(builder -> builder.cell(92))
            .addAlly(builder -> builder.cell(77))
            .addEnemy(builder -> builder.cell(125))
        );

        assertCast(584, 64);

        assertInCastEffectArea(122, 78, 92, 77);
        assertNotInCastEffectArea(125);

        assertEquals(584, ai.get(CastSpell.LAST_CAST).spell().id());
        assertEquals(64, ai.get(CastSpell.LAST_CAST).target().id());
    }

    @Test
    void selfBoostShouldIgnoreSpellWithoutDelay() throws NoSuchFieldException, IllegalAccessException {
        action = Boost.self(container.get(Simulator.class));

        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(148)) // Amplification
            .addEnemy(builder -> builder.cell(125))
        );

        removeSpell(6);

        assertDotNotGenerateAction();
        assertNull(ai.get(CastSpell.LAST_CAST));
    }

    @Test
    void allyBoostShouldAllowSpellWithoutDelay() throws NoSuchFieldException, IllegalAccessException {
        action = Boost.allies(container.get(Simulator.class));

        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(148, 5)) // Amplification
            .addAlly(builder -> builder.cell(136))
            .addEnemy(builder -> builder.cell(125))
        );

        removeSpell(6);

        assertCast(148, 136);

        assertEquals(148, ai.get(CastSpell.LAST_CAST).spell().id());
        assertEquals(136, ai.get(CastSpell.LAST_CAST).target().id());
    }

    @Test
    void allyBoostShouldShouldIgnoreBoostSpellIfGeneratesTooManyDamage() throws NoSuchFieldException, IllegalAccessException {
        action = Boost.allies(container.get(Simulator.class));

        // Ally is on adjacent cell
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(145).charac(Characteristic.AGILITY, 300)) // épée divine
            .addAlly(builder -> builder.cell(136).currentLife(200))
            .addEnemy(builder -> builder.cell(125))
        );

        removeSpell(6);

        assertDotNotGenerateAction();

        // Ally is away
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(145)) // épée divine
            .addAlly(builder -> builder.cell(95))
            .addEnemy(builder -> builder.cell(125))
        );

        assertCast(145, 122);

        assertEquals(145, ai.get(CastSpell.LAST_CAST).spell().id());
        assertEquals(122, ai.get(CastSpell.LAST_CAST).target().id());
    }

    @Test
    void allyBoostShouldAllowsDamageIfLow() throws NoSuchFieldException, IllegalAccessException {
        action = Boost.allies(container.get(Simulator.class));

        // Ally is on adjacent cell
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(145).charac(Characteristic.AGILITY, -50)) // épée divine
            .addAlly(builder -> builder.cell(136))
            .addEnemy(builder -> builder.cell(125))
        );

        removeSpell(6);

        assertCast(145, 122);
        assertInCastEffectArea(136);

        assertEquals(145, ai.get(CastSpell.LAST_CAST).spell().id());
        assertEquals(122, ai.get(CastSpell.LAST_CAST).target().id());
    }

    @Test
    void allyBoostShouldFavorBoostWithoutDamage() throws NoSuchFieldException, IllegalAccessException {
        action = Boost.allies(container.get(Simulator.class));

        // Ally is on adjacent cell
        // divine sword (145) and amplification (148)
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(145, 5).spell(148).charac(Characteristic.AGILITY, -50))
            .addAlly(builder -> builder.cell(136))
            .addEnemy(builder -> builder.cell(125))
        );

        removeSpell(6);

        assertCast(148, 136);

        assertEquals(148, ai.get(CastSpell.LAST_CAST).spell().id());
        assertEquals(136, ai.get(CastSpell.LAST_CAST).target().id());
    }

    @Test
    void allyBoostShouldShouldIgnoreBoostSpellIfKillAlly() throws NoSuchFieldException, IllegalAccessException {
        action = Boost.allies(container.get(Simulator.class));

        // Ally is on adjacent cell
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(145)) // épée divine
            .addAlly(builder -> builder.cell(136).currentLife(1))
            .addEnemy(builder -> builder.cell(125))
        );

        removeSpell(6);

        assertDotNotGenerateAction();
    }

    @Test
    void scoreShouldHandleSpellAPCost() {
        Boost action = Boost.self(container.get(Simulator.class));

        Spell spell = Mockito.mock(Spell.class);

        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122))
            .addEnemy(builder -> builder.player(other).cell(125))
        );

        CastSimulation simulation = new CastSimulation(spell, fighter, fight.map().get(125));

        simulation.addBoost(5, fighter);

        Mockito.when(spell.apCost()).thenReturn(3);
        assertEquals(3.33, action.score(simulation), 0.01);

        Mockito.when(spell.apCost()).thenReturn(4);
        assertEquals(2.5, action.score(simulation), 0.01);

        Mockito.when(spell.apCost()).thenReturn(3);
        simulation.alterActionPoints(1);
        assertEquals(5, action.score(simulation), 0.01);
    }

    @Test
    void scoreShouldPrioritizeMainAlly() {
        Boost action = Boost.allies(container.get(Simulator.class));

        Spell spell = Mockito.mock(Spell.class);

        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122))
            .addEnemy(builder -> builder.player(other).cell(125))
        );

        DoubleFighter invoc = new DoubleFighter(-10, player.fighter());
        fight.fighters().joinTurnList(invoc, fight.map().get(126));
        configureFighterAi(invoc);

        CastSimulation simulation = new CastSimulation(spell, invoc, fight.map().get(122));
        simulation.setMainAlly(player.fighter());
        simulation.addBoost(5, player.fighter());
        Mockito.when(spell.apCost()).thenReturn(3);
        assertEquals(6.66, action.score(simulation), 0.01);

        simulation = new CastSimulation(spell, invoc, fight.map().get(122));
        simulation.addBoost(5, player.fighter());
        Mockito.when(spell.apCost()).thenReturn(3);
        assertEquals(3.33, action.score(simulation), 0.01);
    }
}
