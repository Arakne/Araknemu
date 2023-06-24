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
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.network.game.out.fight.action;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FinishFightActionTest {
    @Test
    void generateMove() {
        Action action = Mockito.mock(Action.class);
        Fighter fighter = Mockito.mock(Fighter.class);

        Mockito.when(action.performer()).thenReturn(fighter);
        Mockito.when(action.type()).thenReturn(ActionType.MOVE);
        Mockito.when(fighter.id()).thenReturn(123);

        assertEquals("GAF2|123", new FinishFightAction(action).toString());
    }

    @Test
    void generateOther() {
        Action action = Mockito.mock(Action.class);
        Fighter fighter = Mockito.mock(Fighter.class);

        Mockito.when(action.performer()).thenReturn(fighter);
        Mockito.when(action.type()).thenReturn(ActionType.CAST);
        Mockito.when(fighter.id()).thenReturn(123);

        assertEquals("GAF0|123", new FinishFightAction(action).toString());
    }
}