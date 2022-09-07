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

import fr.quatrevieux.araknemu.game.fight.ai.AiBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.spell.Spell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DebuffTest extends AiBaseCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushFunctionalSpells();
        action = new Debuff<>(container.get(Simulator.class));
    }

    @Test
    void success() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(312).spell(81))
            .addEnemy(builder -> builder.cell(270))
        );

        assertCast(81, 270);
    }

    @Test
    void allowSelfDebuffIfLessThanEnemies() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(307).spell(168))
            .addEnemy(builder -> builder.cell(322))
            .addEnemy(builder -> builder.cell(308))
        );

        assertCast(168, 293);
    }

    @Test
    void denySelfKill() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(307).spell(168).currentLife(5))
            .addEnemy(builder -> builder.cell(322))
            .addEnemy(builder -> builder.cell(308))
        );

        assertDotNotGenerateAction();
    }

    @Test
    void denyAllyKill() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(250).spell(168))
            .addAlly(builder -> builder.cell(307).currentLife(5))
            .addEnemy(builder -> builder.cell(322))
            .addEnemy(builder -> builder.cell(308))
        );

        assertDotNotGenerateAction();
    }

    @Test
    void notEnoughAP() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(168))
            .addEnemy(builder -> builder.cell(125))
        );

        setAP(1);

        assertDotNotGenerateAction();
    }

    @Test
    void notAP() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(168))
            .addEnemy(builder -> builder.cell(125))
        );
        removeAllAP();

        assertDotNotGenerateAction();
    }

    @Test
    void withAreaSpell() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(168))
            .addEnemy(builder -> builder.cell(78))
            .addEnemy(builder -> builder.cell(92))
            .addEnemy(builder -> builder.cell(77))
            .addAlly(builder -> builder.cell(125))
        );

        assertCast(168, 78);

        assertInCastEffectArea(78, 92, 77);
        assertNotInCastEffectArea(125, 122);
    }

    @Test
    void scoreShouldHandleSpellAPCost() {
        Debuff action = (Debuff) this.action;
        Spell spell = Mockito.mock(Spell.class);

        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122))
            .addEnemy(builder -> builder.player(other).cell(125))
        );

        CastSimulation simulation = new CastSimulation(spell, fighter, fight.map().get(125));

        simulation.addBoost(-5, other.fighter());

        Mockito.when(spell.apCost()).thenReturn(3);
        assertEquals(1.67, action.score(simulation), 0.01);

        Mockito.when(spell.apCost()).thenReturn(4);
        assertEquals(1.25, action.score(simulation), 0.01);

        Mockito.when(spell.apCost()).thenReturn(3);
        simulation.alterActionPoints(1);
        assertEquals(2.5, action.score(simulation), 0.01);
    }

    @Test
    void score() throws SQLException {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122))
            .addEnemy(builder -> builder.player(other).cell(125))
        );

        assertEquals(222.222, computeScore(81, 125), 0.001); // 200 / 0.9 AP (1 bonus AP 10%)
        assertEquals(-222.222, computeScore(81, 122), 0.001); // Self target
        assertEquals(30, computeScore(168, 125), 0.001); // 90 / 3 AP
    }
}
