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

package fr.quatrevieux.araknemu.game.fight.ai.action.logic;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.action.ActionGenerator;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GeneratorAggregateTest {
    @Test
    void initializeShouldInitializeAllActions() {
        ActionGenerator g1 = Mockito.mock(ActionGenerator.class);
        ActionGenerator g2 = Mockito.mock(ActionGenerator.class);
        ActionGenerator g3 = Mockito.mock(ActionGenerator.class);

        AI ai = Mockito.mock(AI.class);

        GeneratorAggregate aggregate = new GeneratorAggregate(new ActionGenerator[] {g1, g2, g3});
        aggregate.initialize(ai);

        Mockito.verify(g1).initialize(ai);
        Mockito.verify(g2).initialize(ai);
        Mockito.verify(g3).initialize(ai);
    }

    @Test
    void generateShouldStopAndReturnTheFirstSuccessfulAction() {
        ActionGenerator g1 = Mockito.mock(ActionGenerator.class);
        ActionGenerator g2 = Mockito.mock(ActionGenerator.class);
        ActionGenerator g3 = Mockito.mock(ActionGenerator.class);

        AI ai = Mockito.mock(AI.class);

        Action action = Mockito.mock(Action.class);

        Mockito.when(g1.generate(ai)).thenReturn(Optional.empty());
        Mockito.when(g2.generate(ai)).thenReturn(Optional.of(action));

        GeneratorAggregate aggregate = new GeneratorAggregate(new ActionGenerator[] {g1, g2, g3});

        assertSame(action, aggregate.generate(ai).get());

        Mockito.verify(g3, Mockito.never()).generate(ai);
    }

    @Test
    void generateAllFailedShouldReturnEmptyOptional() {
        ActionGenerator g1 = Mockito.mock(ActionGenerator.class);
        ActionGenerator g2 = Mockito.mock(ActionGenerator.class);
        ActionGenerator g3 = Mockito.mock(ActionGenerator.class);

        AI ai = Mockito.mock(AI.class);

        Mockito.when(g1.generate(ai)).thenReturn(Optional.empty());
        Mockito.when(g2.generate(ai)).thenReturn(Optional.empty());
        Mockito.when(g3.generate(ai)).thenReturn(Optional.empty());

        GeneratorAggregate aggregate = new GeneratorAggregate(new ActionGenerator[] {g1, g2, g3});

        assertFalse(aggregate.generate(ai).isPresent());
    }
}
