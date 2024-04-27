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
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.quatrevieux.araknemu.game.fight.ai.AiBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.invocation.DoubleFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MoveNearAllyTest extends AiBaseCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        action = new MoveNearAlly();
    }

    @Test
    void success() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(125))
            .addEnemy(builder -> builder.cell(254))
        );

        Fighter invoc = createInvocation(122);
        assertEquals(6, distance(invoc, player.fighter()));

        generateAndPerformMove();

        assertEquals(3, distance(invoc, player.fighter()));
        assertEquals(138, invoc.cell().id());
        assertEquals(0, turn.points().movementPoints());
    }

    @Test
    void withAllyOnPathShouldBeCircumvented() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(181))
            .addAlly(builder -> builder.cell(166))
            .addEnemy(builder -> builder.cell(325))
        );

        Fighter invoc = createInvocation(151);
        assertEquals(2, distance(invoc, player.fighter()));

        generateAndPerformMove();

        assertEquals(1, distance(invoc, player.fighter()));
        assertEquals(195, invoc.cell().id());
        assertEquals(0, turn.points().movementPoints());
    }

    @Test
    void whenAllyBlockAccess() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(341))
            .addAlly(builder -> builder.cell(284))
            .addEnemy(builder -> builder.cell(45))
        );

        Fighter invoc = createInvocation(211);
        assertEquals(9, distance(invoc, player.fighter()));

        generateAndPerformMove();

        assertEquals(6, distance(invoc, player.fighter()));
        assertEquals(256, invoc.cell().id());
        assertEquals(0, turn.points().movementPoints());
    }

    // See: https://github.com/Arakne/Araknemu/issues/94
    @Test
    void notAccessibleCellShouldTruncateToNearestCell() {
        configureFight(fb -> fb
            .map(10342)
            .addSelf(builder -> builder.cell(69))
            .addEnemy(builder -> builder.cell(75))
        );

        Fighter invoc = createInvocation(155);
        assertEquals(6, distance(invoc, player.fighter()));

        generateAndPerformMove();

        assertEquals(4, distance(invoc, player.fighter()));
        assertEquals(126, invoc.cell().id());
        assertEquals(1, turn.points().movementPoints());
    }

    @Test
    void noMP() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(125))
            .addEnemy(builder -> builder.cell(321))
        );

        Fighter invoc = createInvocation(122);

        removeAllMP();

        assertDotNotGenerateAction();
    }

    @Test
    void onAdjacentCell() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(125))
            .addEnemy(builder -> builder.cell(321))
        );

        Fighter invoc = createInvocation(110);

        assertDotNotGenerateAction();
    }

    @Test
    void noAllyDefined() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(125))
            .addEnemy(builder -> builder.cell(321))
        );

        assertDotNotGenerateAction();
    }

    @Test
    void allyHidden() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(125))
            .addEnemy(builder -> builder.cell(254))
        );

        player.fighter().setHidden(player.fighter(), true);

        Fighter invoc = createInvocation(122);
        assertDotNotGenerateAction();
    }

    @Test
    void shouldNotMoveIfAlreadyOnBestCellAndOneCellIsFreeAroundTheAlly() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(210))
            .addEnemy(builder -> builder.cell(250))
        );

        Fighter invoc = createInvocation(195);

        assertDotNotGenerateAction();
        assertEquals(1, distance(invoc, player.fighter()));
    }

    @Test
    void shouldNotGoToAdjacentCellIfBlock() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(371))
            .addEnemy(builder -> builder.cell(250))
        );

        Fighter invoc = createInvocation(313);

        generateAndPerformMove();

        assertEquals(341, fighter.cell().id());
        assertEquals(1, turn.points().movementPoints());

        assertEquals(2, distance(invoc, player.fighter()));
    }

    private Fighter createInvocation(FightCell cell) {
        DoubleFighter invoc = new DoubleFighter(-10, player.fighter());
        fight.fighters().joinTurnList(invoc, cell);
        invoc.init();

        configureFighterAi(invoc);

        return invoc;
    }

    private Fighter createInvocation(int cell) {
        return createInvocation(fight.map().get(cell));
    }
}
