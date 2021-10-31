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
import fr.quatrevieux.araknemu.game.admin.AdminSessionService;
import fr.quatrevieux.araknemu.game.admin.AdminUser;
import fr.quatrevieux.araknemu.game.admin.Command;
import fr.quatrevieux.araknemu.game.admin.account.AccountContext;
import fr.quatrevieux.araknemu.game.admin.account.AccountContextResolver;
import fr.quatrevieux.araknemu.game.admin.context.Context;
import fr.quatrevieux.araknemu.game.admin.context.AbstractContextConfigurator;
import fr.quatrevieux.araknemu.game.admin.exception.CommandNotFoundException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;
import fr.quatrevieux.araknemu.game.handler.event.Disconnected;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlayerContextResolverTest extends GameBaseCase {
    private PlayerContextResolver resolver;
    private AdminUser adminUser;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.use(Player.class);

        resolver = new PlayerContextResolver(
            container.get(PlayerService.class),
            container.get(AccountContextResolver.class)
        );
        adminUser = container.get(AdminSessionService.class).user(gamePlayer(true));
    }

    @Test
    void resolveByGamePlayer() throws SQLException, ContainerException, ContextException {
        Context context = resolver.resolve(gamePlayer());

        assertInstanceOf(PlayerContext.class, context);
        assertSame(gamePlayer(), ((PlayerContext) context).player());
        assertInstanceOf(AccountContext.class, context.child("account"));
    }

    @Test
    void resolveByName() throws SQLException, ContainerException, ContextException {
        Context context = resolver.resolve(adminUser, () -> "Bob");

        assertInstanceOf(PlayerContext.class, context);
        assertInstanceOf(AccountContext.class, context.child("account"));
    }

    @Test
    void resolvePlayerNotFound() {
        assertThrowsWithMessage(ContextException.class, "Cannot found the player notFound", () -> resolver.resolve(adminUser, () -> "notFound"));
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

        GamePlayer player = gamePlayer(true);

        Context context = resolver.resolve(adminUser, player::name);

        assertSame(command, context.command("mocked"));
    }

    @Test
    void resolveShouldKeepContextInstance() throws ContextException, CommandNotFoundException, SQLException {
        Context context1 = resolver.resolve(adminUser, () -> "Bob");
        Context context2 = resolver.resolve(adminUser, () -> "Bob");

        assertSame(context1, context2);
        assertSame(context1.command("info"), context2.command("info"));

        PlayerContext.class.cast(context1).player().dispatcher().dispatch(new Disconnected());
        container.get(PlayerService.class).load(session, gamePlayer().id());

        assertNotSame(context1, resolver.resolve(adminUser, () -> "Bob"));
    }
}
