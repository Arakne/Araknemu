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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.quatrevieux.araknemu.game.fight.ai.AiBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MoveNearAlliesTest extends AiBaseCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        action = new MoveNearAllies();
    }

    @Test
    void success() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122))
            .addAlly(builder -> builder.cell(125))
            .addEnemy(builder -> builder.cell(126))
        );

        assertEquals(6, distance(getAlly(1)));

        generateAndPerformMove();

        assertEquals(80, fighter.cell().id());
        assertEquals(0, turn.points().movementPoints());

        assertEquals(3, distance(getAlly(1)));
    }

    @Test
    void shouldMoveNearNearestAlly() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(284))
            .addAlly(builder -> builder.cell(384))
            .addAlly(builder -> builder.cell(272))
            .addEnemy(builder -> builder.cell(126))
        );

        assertEquals(7, distance(getAlly(1)));
        assertEquals(5, distance(getAlly(2)));

        generateAndPerformMove();

        assertEquals(271, fighter.cell().id());
        assertEquals(0, turn.points().movementPoints());

        assertEquals(8, distance(getAlly(1)));
        assertEquals(2, distance(getAlly(2)));
    }

    @Test
    void shouldFavorBestAverageDistance() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(298))
            .addAlly(builder -> builder.cell(356))
            .addAlly(builder -> builder.cell(226))
            .addAlly(builder -> builder.cell(211))
            .addEnemy(builder -> builder.cell(126))
        );

        assertEquals(4, distance(getAlly(1)));
        assertEquals(5, distance(getAlly(2)));
        assertEquals(6, distance(getAlly(3)));

        generateAndPerformMove();

        assertEquals(255, fighter.cell().id());
        assertEquals(0, turn.points().movementPoints());

        assertEquals(7, distance(getAlly(1)));
        assertEquals(2, distance(getAlly(2)));
        assertEquals(3, distance(getAlly(3)));
    }

    @Test
    void noMP() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122))
            .addEnemy(builder -> builder.player(other).cell(125))
            .addAlly(builder -> builder.cell(126))
        );

        removeAllMP();

        assertDotNotGenerateAction();
    }

    @Test
    void blocked() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(384))
            .addEnemy(builder -> builder.cell(370))
            .addAlly(builder -> builder.cell(126))
        );

        assertDotNotGenerateAction();
    }

    @Test
    void noAllies() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(384))
            .addEnemy(builder -> builder.cell(250))
        );

        assertDotNotGenerateAction();
    }

    @Test
    void alreadyNearAlly() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(356))
            .addAlly(builder -> builder.cell(342))
            .addEnemy(builder -> builder.cell(250))
        );

        assertDotNotGenerateAction();
    }

    private int distance(Fighter other) {
        return fighter.cell().coordinate().distance(other.cell());
    }
}