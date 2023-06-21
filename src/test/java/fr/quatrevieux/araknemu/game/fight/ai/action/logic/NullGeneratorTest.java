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
import fr.quatrevieux.araknemu.game.fight.ai.action.AiActionFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

class NullGeneratorTest {
    @Test
    void initializeShouldDoNothing() {
        AI ai = Mockito.mock(AI.class);
        NullGenerator.INSTANCE.initialize(ai);

        Mockito.verifyNoInteractions(ai);
    }

    @Test
    void shouldAlwaysGeneratesEmpty() {
        assertFalse(NullGenerator.INSTANCE.generate(Mockito.mock(AI.class), Mockito.mock(AiActionFactory.class)).isPresent());
    }

    @Test
    void get() {
        assertSame(NullGenerator.get(), NullGenerator.get());
    }
}
