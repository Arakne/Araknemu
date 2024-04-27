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

package fr.quatrevieux.araknemu.game.fight.ai.action.util;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BattlefieldTest extends FightBaseCase {
    @Test
    void freeAdjacentCellsCount() {
        Fight fight = fightBuilder()
            .addSelf(builder -> builder.cell(256))
            .addEnemy(builder -> builder.cell(241))
            .addEnemy(builder -> builder.cell(271))
            .addAlly(builder -> builder.cell(207))
            .build(true)
        ;

        fight.nextState();

        assertEquals(1, Battlefield.freeAdjacentCellsCount(fight.map().get(256)));
        assertEquals(3, Battlefield.freeAdjacentCellsCount(fight.map().get(241)));
        assertEquals(1, Battlefield.freeAdjacentCellsCount(fight.map().get(271)));
        assertEquals(2, Battlefield.freeAdjacentCellsCount(fight.map().get(270)));
        assertEquals(4, Battlefield.freeAdjacentCellsCount(fight.map().get(207)));
    }
}
