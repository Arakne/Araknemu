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
import fr.quatrevieux.araknemu.game.fight.castable.spell.SpellConstraintsValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AttractEnemyTest extends AiBaseCase {
    private AttractEnemy action;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushFunctionalSpells();
        super.action = this.action = new AttractEnemy(6);
    }

    @Test
    void success() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(181).spell(434, 5))
            .addEnemy(builder -> builder.player(other).cell(27))
        );

        assertCast(434, 27);

        AttractEnemy.Result result = action.compute(ai, new SpellConstraintsValidator(fight));

        assertTrue(result.valid());
        assertEquals(27, result.target().id());
        assertEquals(434, result.spell().id());
        assertEquals(5, result.distance());
    }

    @Test
    void cantCast() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(181).spell(434, 5))
            .addEnemy(builder -> builder.player(other).cell(96))
        );

        assertDotNotGenerateAction();

        AttractEnemy.Result result = action.compute(ai, new SpellConstraintsValidator(fight));
        assertFalse(result.valid());
    }

    @Test
    void notEnoughAP() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(181).spell(434, 5))
            .addEnemy(builder -> builder.player(other).cell(111))
        );

        turn.points().useActionPoints(5);
        assertDotNotGenerateAction();

        AttractEnemy.Result result = action.compute(ai, new SpellConstraintsValidator(fight));
        assertFalse(result.valid());
    }

    @Test
    void notAP() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(181).spell(434, 5))
            .addEnemy(builder -> builder.player(other).cell(111))
        );

        turn.points().useActionPoints(100);
        assertDotNotGenerateAction();

        AttractEnemy.Result result = action.compute(ai, new SpellConstraintsValidator(fight));
        assertFalse(result.valid());
    }

    @Test
    void noAttractionSpell() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(181))
            .addEnemy(builder -> builder.player(other).cell(111))
        );

        assertDotNotGenerateAction();

        AttractEnemy.Result result = action.compute(ai, new SpellConstraintsValidator(fight));
        assertFalse(result.valid());
    }

    @Test
    void enemyAlreadyOnAdjacentCell() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(181).spell(434, 5))
            .addEnemy(builder -> builder.player(other).cell(196))
            .addEnemy(builder -> builder.cell(111))
        );

        assertDotNotGenerateAction();

        AttractEnemy.Result result = action.compute(ai, new SpellConstraintsValidator(fight));
        assertFalse(result.valid());
    }

    @Test
    void ignoreIfDoesNotMoveEnemy() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(250).spell(434, 5))
            .addEnemy(builder -> builder.player(other).cell(325))
        );

        assertDotNotGenerateAction();

        AttractEnemy.Result result = action.compute(ai, new SpellConstraintsValidator(fight));
        assertFalse(result.valid());
    }

    @Test
    void shouldPrioritizeNearestEnemy() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(165).spell(434, 5))
            .addEnemy(builder -> builder.cell(210))
            .addEnemy(builder -> builder.player(other).cell(305))
        );

        assertCast(434, 210);

        AttractEnemy.Result result = action.compute(ai, new SpellConstraintsValidator(fight));

        assertTrue(result.valid());
        assertEquals(210, result.target().id());
        assertEquals(434, result.spell().id());
        assertEquals(1, result.distance());
    }

    @Test
    void shouldPrioritizeLowerApCost() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(165)
                .spell(434, 5) // 3 AP
                .spell(344) // 2 AP
            )
            .addEnemy(builder -> builder.cell(210))
        );

        assertCast(344, 210);

        AttractEnemy.Result result = action.compute(ai, new SpellConstraintsValidator(fight));

        assertTrue(result.valid());
        assertEquals(210, result.target().id());
        assertEquals(344, result.spell().id());
        assertEquals(1, result.distance());
    }
}
