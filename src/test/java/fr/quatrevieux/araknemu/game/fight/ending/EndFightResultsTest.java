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

package fr.quatrevieux.araknemu.game.fight.ending;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.FighterOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertSame;

class EndFightResultsTest extends FightBaseCase {
    private Fight fight;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
    }

    @Test
    void applyToLoosers() {
        Fighter winner = Mockito.mock(Fighter.class);
        Fighter looser = Mockito.mock(Fighter.class);

        FighterOperation operation = Mockito.mock(FighterOperation.class);

        EndFightResults results = new EndFightResults(fight, Collections.singletonList(winner), Collections.singletonList(looser));

        assertSame(operation, results.applyToLoosers(operation));
        Mockito.verify(winner, Mockito.never()).apply(operation);
        Mockito.verify(looser).apply(operation);
    }

    @Test
    void applyToWinners() {
        Fighter winner = Mockito.mock(Fighter.class);
        Fighter looser = Mockito.mock(Fighter.class);

        FighterOperation operation = Mockito.mock(FighterOperation.class);

        EndFightResults results = new EndFightResults(fight, Collections.singletonList(winner), Collections.singletonList(looser));

        assertSame(operation, results.applyToWinners(operation));
        Mockito.verify(winner).apply(operation);
        Mockito.verify(looser, Mockito.never()).apply(operation);
    }
}