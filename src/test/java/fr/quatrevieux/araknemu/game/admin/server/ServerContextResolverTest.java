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

package fr.quatrevieux.araknemu.game.admin.server;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.AdminSessionService;
import fr.quatrevieux.araknemu.game.admin.AdminUser;
import fr.quatrevieux.araknemu.game.admin.Command;
import fr.quatrevieux.araknemu.game.admin.context.AbstractContextConfigurator;
import fr.quatrevieux.araknemu.game.admin.context.Context;
import fr.quatrevieux.araknemu.game.admin.context.NullContext;
import fr.quatrevieux.araknemu.game.admin.exception.CommandNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertSame;

class ServerContextResolverTest extends GameBaseCase {
    private ServerContextResolver resolver;
    private AdminUser adminUser;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        resolver = new ServerContextResolver(new NullContext());
        adminUser = container.get(AdminSessionService.class).user(gamePlayer());
    }

    @Test
    void resolve() throws ContainerException {
        Context context = resolver.resolve(adminUser, () -> null);

        assertInstanceOf(ServerContext.class, context);
    }

    @Test
    void register() throws CommandNotFoundException, SQLException {
        Command command = Mockito.mock(Command.class);
        Mockito.when(command.name()).thenReturn("mocked");

        resolver.register(new AbstractContextConfigurator<ServerContext>() {
            @Override
            public void configure(ServerContext context) {
                add(command);
            }
        });

        Context context = resolver.resolve(adminUser, () -> null);

        assertSame(command, context.command("mocked"));
    }

    @Test
    void resolveShouldKeepInstance() throws ContainerException, CommandNotFoundException {
        resolver.register(new AbstractContextConfigurator<ServerContext>() {
            @Override
            public void configure(ServerContext context) {
                add(new AbstractCommand<Void>() {
                    @Override
                    protected void build(Builder builder) {}

                    @Override
                    public String name() {
                        return "test";
                    }

                    @Override
                    public void execute(AdminPerformer performer, Void arguments) {}
                });
            }
        });

        Context context = resolver.resolve(adminUser, () -> null);
        Context context2 = resolver.resolve(adminUser, () -> null);

        assertSame(context, context2);
        assertSame(context.command("test"), context2.command("test"));
    }
}
