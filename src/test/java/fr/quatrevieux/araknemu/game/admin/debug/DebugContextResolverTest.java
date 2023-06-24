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

package fr.quatrevieux.araknemu.game.admin.debug;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.admin.Command;
import fr.quatrevieux.araknemu.game.admin.context.AbstractContextConfigurator;
import fr.quatrevieux.araknemu.game.admin.context.Context;
import fr.quatrevieux.araknemu.game.admin.context.NullContext;
import fr.quatrevieux.araknemu.game.admin.exception.CommandNotFoundException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertSame;

class DebugContextResolverTest extends GameBaseCase {
    private DebugContextResolver resolver;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        resolver = new DebugContextResolver(new NullContext());
    }

    @Test
    void resolve() throws ContextException {
        assertInstanceOf(DebugContext.class, resolver.resolve(null, null));
    }

    @Test
    void register() throws ContextException, CommandNotFoundException {
        Command command = Mockito.mock(Command.class);
        Mockito.when(command.name()).thenReturn("mocked");

        resolver.register(new AbstractContextConfigurator<DebugContext>() {
            @Override
            public void configure(DebugContext context) {
                add(command);
            }
        });

        Context context = resolver.resolve(null, null);

        assertSame(command, context.command("mocked"));
    }
}
