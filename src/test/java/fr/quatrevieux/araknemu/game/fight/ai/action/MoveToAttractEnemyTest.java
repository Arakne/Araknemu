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

package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.quatrevieux.araknemu.game.fight.ai.AiBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveToAttractEnemyTest extends AiBaseCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushFunctionalSpells();

        action = new MoveToAttractEnemy(new AttractEnemy(6));
    }

    @Test
    void success() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(91).spell(434, 5))
            .addEnemy(builder -> builder.player(other).cell(183))
        );

        generateAndPerformMove();

        assertEquals(78, fighter.cell().id());
    }

    @Test
    void shouldPrioritizeNearestMove() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(153).spell(434, 5))
            .addEnemy(builder -> builder.cell(49))
            .addEnemy(builder -> builder.cell(91))
        );

        generateAndPerformMove();

        assertEquals(139, fighter.cell().id());
    }

    @Test
    void shouldNotGenerateIfHasNoEffect() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(220).spell(434, 5))
            .addEnemy(builder -> builder.player(other).cell(325))
        );

        assertDotNotGenerateAction();
    }

    @Test
    void shouldNotGenerateIfMoveCanReachEnemy() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(167).spell(434, 5))
            .addEnemy(builder -> builder.player(other).cell(227))
        );

        assertDotNotGenerateAction();
    }

    @Test
    void shouldNotGenerateIfNearestEnemyIsNearThanResultingDistance() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(180).spell(434, 4))
            .addEnemy(builder -> builder.player(other).cell(255))
            .addEnemy(builder -> builder.cell(26))
        );

        assertDotNotGenerateAction();
    }

    @Test
    void shouldNotGenerateIfHasNoAp() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(180).spell(434, 4))
            .addEnemy(builder -> builder.cell(26))
        );

        fighter.turn().points().useActionPoints(100);

        assertDotNotGenerateAction();
    }

    @Test
    void shouldNotGenerateIfHasNoAttractionSpell() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(180))
            .addEnemy(builder -> builder.cell(26))
        );

        assertDotNotGenerateAction();
    }
}
