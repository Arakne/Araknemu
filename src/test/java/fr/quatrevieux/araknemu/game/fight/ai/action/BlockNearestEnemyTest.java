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

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.ai.AiBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.invocation.DoubleFighter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BlockNearestEnemyTest extends AiBaseCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        action = new BlockNearestEnemy();
    }

    @Test
    void shouldMoveToEnemyNearTheInvoker() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(94).charac(Characteristic.AGILITY, 100))
            .addEnemy(builder -> builder.cell(124))
            .addEnemy(builder -> builder.cell(167))
        );

        DoubleFighter invoc = new DoubleFighter(-10, player.fighter());
        fight.fighters().joinTurnList(invoc, fight.map().get(152)); // Adjacent to enemy 167
        invoc.init();

        configureFighterAi(invoc);

        generateAndPerformMove();

        assertEquals(138, fighter.cell().id());
        assertEquals(2, turn.points().movementPoints());
    }

    @Test
    void shouldMoveToNearestEnemyIfInvokerIsInvisible() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(152).charac(Characteristic.AGILITY, 100))
            .addEnemy(builder -> builder.cell(167))
            .addEnemy(builder -> builder.cell(212))
        );

        player.fighter().setHidden(player.fighter(), true);

        DoubleFighter invoc = new DoubleFighter(-10, player.fighter());
        fight.fighters().joinTurnList(invoc, fight.map().get(183));
        invoc.init();

        configureFighterAi(invoc);

        generateAndPerformMove();

        assertEquals(197, fighter.cell().id());
        assertEquals(2, turn.points().movementPoints());
    }

    @Test
    void shouldMoveToNearestEnemyIfNotInvoked() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(152).charac(Characteristic.AGILITY, 100))
            .addEnemy(builder -> builder.cell(182))
            .addEnemy(builder -> builder.cell(110))
        );

        generateAndPerformMove();

        assertEquals(167, fighter.cell().id());
        assertEquals(2, turn.points().movementPoints());
    }
}
