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

package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.game.fight.ai.AiBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.Cast;
import fr.quatrevieux.araknemu.game.spell.Spell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HealTest extends AiBaseCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        action = new Heal<>(container.get(Simulator.class));
        dataSet.pushFunctionalSpells();
    }

    @Test
    void success() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).currentLife(50).maxLife(100).spell(121).spell(131))
            .addEnemy(builder -> builder.player(other).cell(125))
        );

        assertCast(121, 122);
    }

    @Test
    void notEnoughAP() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).currentLife(50).maxLife(100).spell(121).spell(131))
            .addEnemy(builder -> builder.player(other).cell(125))
        );

        turn.points().useActionPoints(5);
        assertDotNotGenerateAction();
    }

    @Test
    void notAP() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).currentLife(50).maxLife(100).spell(121).spell(131))
            .addEnemy(builder -> builder.player(other).cell(125))
        );

        turn.points().useActionPoints(6);
        assertDotNotGenerateAction();
    }

    @Test
    void shouldIgnoreIfHealMostlyEnemies() throws SQLException {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(327).currentLife(50).maxLife(100).spell(130))
            .addEnemy(builder -> builder.cell(341).currentLife(50).maxLife(100))
            .addEnemy(builder -> builder.cell(312).currentLife(50).maxLife(100))
            .addEnemy(builder -> builder.cell(313).currentLife(50).maxLife(100))
            .addEnemy(builder -> builder.cell(342).currentLife(50).maxLife(100))
        );

        assertDotNotGenerateAction();
    }

    @Test
    void shouldIgnoreIfCanKillAllyOrItself() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(327).currentLife(15).maxLife(100).spell(102))
            .addAlly(builder -> builder.cell(326).currentLife(15).maxLife(100).spell(102))
            .addEnemy(builder -> builder.cell(342))
        );

        assertDotNotGenerateAction();
    }

    @Test
    void withAreaSpell() throws SQLException {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(126).spells(223).currentLife(50).maxLife(100).spell(121).spell(131).spell(130))
            .addAlly(builder -> builder.player(other).cell(125).currentLife(50).maxLife(100))
            .addEnemy(builder -> builder.cell(122))
        );

        Cast cast = generateCast();

        assertEquals(130, cast.spell().id());
        assertEquals(96, cast.target().id());

        assertInCastEffectArea(126, 125);
        assertNotInCastEffectArea(122);
    }

    @Test
    void scoreShouldHandleSpellAPCost() {
        Spell spell = Mockito.mock(Spell.class);

        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).currentLife(50).maxLife(100))
            .addEnemy(builder -> builder.player(other).cell(125))
        );

        CastSimulation simulation = new CastSimulation(spell, fighter, fight.map().get(125));

        simulation.addHeal(new Interval(5, 10), fighter);

        Mockito.when(spell.apCost()).thenReturn(3);
        assertEquals(2.5, Heal.class.cast(action).score(simulation));

        Mockito.when(spell.apCost()).thenReturn(4);
        assertEquals(1.875, Heal.class.cast(action).score(simulation));

        Mockito.when(spell.apCost()).thenReturn(3);
        simulation.alterActionPoints(1);
        assertEquals(3.75, Heal.class.cast(action).score(simulation));
    }

    @Test
    void score() throws SQLException {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).currentLife(50).maxLife(100))
            .addAlly(builder -> builder.cell(123).currentLife(48).maxLife(50))
            .addEnemy(builder -> builder.player(other).cell(125).currentLife(50).maxLife(100))
        );

        assertEquals(9.9275, computeScore(121, 122), 0.001); // 39.5 * 98% + 50 * 2% / 4 AP (39.5 instead of 40 because values are truncated)
        assertEquals(2.50, computeScore(131, 122), 0.001); // (6 * 98% + 12.5 * 2%) * (1 + 3*0.075) / 3 AP : buff is divided by 10, so its rate is 0.075 per turn
        assertEquals(0.5, computeScore(121, 123), 0.001); // 2 (LP lost) / 4 AP
        assertEquals(1.126, computeScore(131, 123), 0.001); // (2 + (6 * 98% + 12.5 * 2%) * (3*0.075)) / 3 AP : buff heal is used as buff, so it's not capped by lost LP
    }
}
