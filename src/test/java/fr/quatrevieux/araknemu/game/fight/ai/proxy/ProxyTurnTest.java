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

package fr.quatrevieux.araknemu.game.fight.ai.proxy;

import fr.quatrevieux.araknemu.game.fight.ai.AiBaseCase;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class ProxyTurnTest extends AiBaseCase {
    @Test
    void baseValues() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(152))
            .addEnemy(builder -> builder.cell(167))
            .addAlly(builder -> builder.cell(166))
        );

        ProxyActiveFighter fighter = new ProxyActiveFighter(ai.fighter());
        ProxyTurn turn = new ProxyTurn(ai, fighter);

        assertSame(fighter, turn.fighter());
        assertTrue(turn.active());
        assertSame(ai.turn().actions(), turn.actions());
        assertSame(ai.turn().points(), turn.points());
    }

    @Test
    void disallowModifications() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(152))
            .addEnemy(builder -> builder.cell(167))
            .addAlly(builder -> builder.cell(166))
        );

        ProxyActiveFighter fighter = new ProxyActiveFighter(ai.fighter());
        ProxyTurn turn = new ProxyTurn(ai, fighter);

        assertThrows(UnsupportedOperationException.class, () -> turn.perform(Mockito.mock(Action.class)));
        assertThrows(UnsupportedOperationException.class, () -> turn.later(() -> {}));
        assertThrows(UnsupportedOperationException.class, turn::stop);
    }
}
