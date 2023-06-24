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
import fr.quatrevieux.araknemu.game.fight.ai.action.AiActionFactory;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;

class ConditionalGeneratorTest {
    @Test
    void initializeShouldInitializeAllActions() {
        ActionGenerator gs = Mockito.mock(ActionGenerator.class);
        ActionGenerator go = Mockito.mock(ActionGenerator.class);

        AI ai = Mockito.mock(AI.class);

        ConditionalGenerator aggregate = new ConditionalGenerator(a -> true, gs, go);
        aggregate.initialize(ai);

        Mockito.verify(gs).initialize(ai);
        Mockito.verify(go).initialize(ai);
    }

    @Test
    void generateSuccess() {
        ActionGenerator gs = Mockito.mock(ActionGenerator.class);
        ActionGenerator go = Mockito.mock(ActionGenerator.class);

        AI ai = Mockito.mock(AI.class);
        AiActionFactory actions = Mockito.mock(AiActionFactory.class);
        Action action = Mockito.mock(Action.class);

        Mockito.when(gs.generate(ai, actions)).thenReturn(Optional.of(action));

        ConditionalGenerator aggregate = new ConditionalGenerator(a -> true, gs, go);

        assertSame(action, aggregate.generate(ai, actions).get());
        Mockito.verify(go, Mockito.never()).generate(ai, actions);
    }

    @Test
    void generateOtherwise() {
        ActionGenerator gs = Mockito.mock(ActionGenerator.class);
        ActionGenerator go = Mockito.mock(ActionGenerator.class);

        AI ai = Mockito.mock(AI.class);
        AiActionFactory actions = Mockito.mock(AiActionFactory.class);
        Action action = Mockito.mock(Action.class);

        Mockito.when(go.generate(ai, actions)).thenReturn(Optional.of(action));

        ConditionalGenerator aggregate = new ConditionalGenerator(a -> false, gs, go);

        assertSame(action, aggregate.generate(ai, actions).get());
        Mockito.verify(gs, Mockito.never()).generate(ai, actions);
    }
}
