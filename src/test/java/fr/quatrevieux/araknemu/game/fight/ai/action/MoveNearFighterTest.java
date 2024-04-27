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
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MoveNearFighterTest extends AiBaseCase {
    @Test
    void success() {
        action = new MoveNearFighter(ai -> Optional.of(fight.map().get(222).fighter()));
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122))
            .addEnemy(builder -> builder.cell(125))
            .addEnemy(builder -> builder.cell(126))
            .addAlly(builder -> builder.cell(222))
        );

        generateAndPerformMove();
        assertEquals(165, fighter.cell().id());
        assertEquals(0, turn.points().movementPoints());
    }

    @Test
    void shouldMoveToAdjacentCellWhenCarriedByTarget() {
        action = new MoveNearFighter(ai -> Optional.of(fight.map().get(222).fighter()));
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122))
            .addAlly(builder -> builder.cell(222))
            .addEnemy(builder -> builder.cell(126))
        );

        fighter.setCell(fight.map().get(222));

        generateAndPerformMove();
        assertEquals(236, fighter.cell().id());
        assertEquals(2, turn.points().movementPoints());
    }

    @Test
    void failedWhenCarriedButNoAvailableAdjacentCell() {
        action = new MoveNearFighter(ai -> Optional.of(fight.map().get(222).fighter()));
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122))
            .addAlly(builder -> builder.cell(222))
            .addEnemy(builder -> builder.cell(236))
            .addEnemy(builder -> builder.cell(207))
        );

        fighter.setCell(fight.map().get(222));

        assertDotNotGenerateAction();
    }

    @Test
    void withAllyOnPathShouldBeCircumvented() {
        action = new MoveNearFighter(ai -> Optional.of(fight.map().get(181).fighter()));
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(151))
            .addAlly(builder -> builder.cell(166))
            .addEnemy(builder -> builder.cell(181))
        );

        generateAndPerformMove();

        assertEquals(195, fighter.cell().id());
        assertEquals(0, turn.points().movementPoints());
    }

    @Test
    void whenAllyBlockAccess() {
        action = new MoveNearFighter(ai -> Optional.of(fight.map().get(341).fighter()));
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(211))
            .addAlly(builder -> builder.cell(284))
            .addEnemy(builder -> builder.cell(341))
        );

        generateAndPerformMove();

        assertEquals(256, fighter.cell().id());
        assertEquals(0, turn.points().movementPoints());
    }

    // See: https://github.com/Arakne/Araknemu/issues/94
    @Test
    void notAccessibleCellShouldTruncateToNearestCell() {
        action = new MoveNearFighter(ai -> Optional.of(fight.map().get(69).fighter()));
        configureFight(fb -> fb
            .map(10342)
            .addSelf(builder -> builder.cell(155))
            .addEnemy(builder -> builder.cell(69))
        );

        generateAndPerformMove();

        assertEquals(126, fighter.cell().id());
        assertEquals(1, turn.points().movementPoints());
    }

    @Test
    void noMP() {
        action = new MoveNearFighter(ai -> Optional.of(fight.map().get(125).fighter()));
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122))
            .addEnemy(builder -> builder.cell(125))
        );

        removeAllMP();

        assertDotNotGenerateAction();
    }

    @Test
    void onAdjacentCell() {
        action = new MoveNearFighter(ai -> Optional.of(fight.map().get(125).fighter()));
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(110))
            .addEnemy(builder -> builder.cell(125))
        );

        assertDotNotGenerateAction();
    }

    @Test
    void noAvailableTarget() {
        action = new MoveNearFighter(ai -> Optional.empty());
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(90))
            .addEnemy(builder -> builder.cell(125))
        );

        assertDotNotGenerateAction();
    }

    @Test
    void withoutAllowBlockingShouldNotGoToAdjacentCellIfBlock() {
        action = new MoveNearFighter(ai -> Optional.ofNullable(fight.map().get(371).fighter()), false);
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(313))
            .addAlly(builder -> builder.cell(371))
            .addEnemy(builder -> builder.cell(250))
        );

        generateAndPerformMove();

        assertEquals(341, fighter.cell().id());
        assertEquals(1, turn.points().movementPoints());
    }
}
