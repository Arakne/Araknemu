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

package fr.quatrevieux.araknemu.game.admin.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.admin.Command;
import fr.quatrevieux.araknemu.game.admin.account.AccountContext;
import fr.quatrevieux.araknemu.game.admin.account.AccountContextResolver;
import fr.quatrevieux.araknemu.game.admin.context.Context;
import fr.quatrevieux.araknemu.game.admin.context.AbstractContextConfigurator;
import fr.quatrevieux.araknemu.game.admin.context.NullContext;
import fr.quatrevieux.araknemu.game.admin.exception.CommandNotFoundException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlayerContextResolverTest extends GameBaseCase {
    private PlayerContextResolver resolver;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.use(Player.class);

        resolver = new PlayerContextResolver(
            container.get(PlayerService.class),
            container.get(AccountContextResolver.class)
        );
    }

    @Test
    void resolveByGamePlayer() throws SQLException, ContainerException, ContextException {
        Context context = resolver.resolve(
            new NullContext(),
            gamePlayer()
        );

        assertInstanceOf(PlayerContext.class, context);
        assertInstanceOf(AccountContext.class, context.child("account"));
    }

    @Test
    void resolveByName() throws SQLException, ContainerException, ContextException {
        gamePlayer(true);

        Context context = resolver.resolve(new NullContext(), "Bob");

        assertInstanceOf(PlayerContext.class, context);
        assertInstanceOf(AccountContext.class, context.child("account"));
    }

    @Test
    void resolveInvalidArgument() {
        assertThrows(ContextException.class, () -> resolver.resolve(new NullContext(), new Object()));
    }

    @Test
    void resolvePlayerNotFound() {
        assertThrows(ContextException.class, () -> resolver.resolve(new NullContext(), "notFound"), "Cannot found the player notFound");
    }

    @Test
    void register() throws ContextException, CommandNotFoundException, SQLException {
        Command command = Mockito.mock(Command.class);
        Mockito.when(command.name()).thenReturn("mocked");

        resolver.register(new AbstractContextConfigurator<PlayerContext>() {
            @Override
            public void configure(PlayerContext context) {
                add(command);
            }
        });

        Context context = resolver.resolve(new NullContext(), gamePlayer(true).name());

        assertSame(command, context.command("mocked"));
    }
}
