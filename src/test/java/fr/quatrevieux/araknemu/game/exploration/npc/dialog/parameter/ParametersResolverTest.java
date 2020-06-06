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

package fr.quatrevieux.araknemu.game.exploration.npc.dialog.parameter;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class ParametersResolverTest extends GameBaseCase {
    private ExplorationPlayer player;
    private ParametersResolver resolver;
    private Logger logger;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        player = explorationPlayer();
        resolver = new ParametersResolver(
            new VariableResolver[] {new GetterResolver("id", ExplorationPlayer::id)},
            logger = Mockito.mock(Logger.class)
        );
    }

    @Test
    void resolveConstant() {
        assertEquals("my constant", resolver.resolve("my constant", player));
        Mockito.verify(logger, Mockito.never()).warn(Mockito.anyString());
    }

    @Test
    void resolveUndefinedVariable() {
        assertEquals("[my_var]", resolver.resolve("[my_var]", player));
        Mockito.verify(logger).warn("Undefined dialog variable {}", "my_var");
    }

    @Test
    void resolveVariable() {
        assertEquals(1, resolver.resolve("[id]", player));
    }
}
