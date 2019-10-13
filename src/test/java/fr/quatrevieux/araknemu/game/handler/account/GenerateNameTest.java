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

package fr.quatrevieux.araknemu.game.handler.account;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.generator.NameGenerationException;
import fr.quatrevieux.araknemu.game.account.generator.NameGenerator;
import fr.quatrevieux.araknemu.core.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.in.account.AskRandomName;
import fr.quatrevieux.araknemu.network.game.out.account.RandomNameGenerated;
import fr.quatrevieux.araknemu.network.game.out.account.RandomNameGenerationError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class GenerateNameTest extends GameBaseCase {
    private GenerateName handler;
    private NameGenerator generator;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new GenerateName(
            generator = Mockito.mock(NameGenerator.class)
        );
    }

    @Test
    void handleSuccess() throws Exception {
        Mockito.when(generator.generate()).thenReturn("Cookie");

        handler.handle(session, new AskRandomName());

        requestStack.assertLast(
            new RandomNameGenerated("Cookie")
        );
    }

    @Test
    void handleError() throws Exception {
        Mockito.when(generator.generate()).thenThrow(new NameGenerationException());

        try {
            handler.handle(session, new AskRandomName());
            fail("An ErrorPacket must be thrown");
        } catch (ErrorPacket e) {
            assertInstanceOf(RandomNameGenerationError.class, e.packet());
        }
    }
}
