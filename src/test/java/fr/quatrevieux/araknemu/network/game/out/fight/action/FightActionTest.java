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
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class FightActionTest {
    @Test
    void generateOnSuccess() {
        assertEquals(
            "GA0;1;123;arg",
            new FightAction(
                new ActionResult() {
                    @Override
                    public int action() {
                        return 1;
                    }

                    @Override
                    public PlayableFighter performer() {
                        PlayableFighter fighter = Mockito.mock(PlayableFighter.class);
                        Mockito.when(fighter.id()).thenReturn(123);

                        return fighter;
                    }

                    @Override
                    public Object[] arguments() {
                        return new Object[] {"arg"};
                    }

                    @Override
                    public boolean success() {
                        return true;
                    }

                    @Override
                    public boolean secret() {
                        return false;
                    }

                    @Override
                    public void apply(FightTurn turn) {

                    }
                }
            ).toString()
        );
    }

    @Test
    void generateOnFail() {
        assertEquals(
            "GA;1;123;arg",
            new FightAction(
                new ActionResult() {
                    @Override
                    public int action() {
                        return 1;
                    }

                    @Override
                    public PlayableFighter performer() {
                        PlayableFighter fighter = Mockito.mock(PlayableFighter.class);
                        Mockito.when(fighter.id()).thenReturn(123);

                        return fighter;
                    }

                    @Override
                    public Object[] arguments() {
                        return new Object[] {"arg"};
                    }

                    @Override
                    public boolean success() {
                        return false;
                    }

                    @Override
                    public boolean secret() {
                        return false;
                    }

                    @Override
                    public void apply(FightTurn turn) {

                    }
                }
            ).toString()
        );
    }
}