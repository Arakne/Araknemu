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

package fr.quatrevieux.araknemu.game.admin;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.network.session.SessionFactory;
import fr.quatrevieux.araknemu.core.network.util.DummyChannel;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.admin.account.AccountContextResolver;
import fr.quatrevieux.araknemu.game.admin.context.AggregationContext;
import fr.quatrevieux.araknemu.game.admin.context.ContextResolver;
import fr.quatrevieux.araknemu.game.admin.context.SelfContextResolver;
import fr.quatrevieux.araknemu.game.admin.debug.DebugContext;
import fr.quatrevieux.araknemu.game.admin.debug.DebugContextResolver;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.exception.CommandException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextNotFoundException;
import fr.quatrevieux.araknemu.game.admin.player.PlayerContext;
import fr.quatrevieux.araknemu.game.admin.player.PlayerContextResolver;
import fr.quatrevieux.araknemu.game.admin.server.ServerContextResolver;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.network.game.GameSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @todo recursive context child
 */
class ContextCommandParserTest extends GameBaseCase {
    private ContextCommandParser parser;
    private AdminUser user;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        user = container.get(AdminSessionService.class).user(gamePlayer());
        parser = new ContextCommandParser(
            performer -> new AggregationContext(
                performer.self(),
                container.get(ServerContextResolver.class).resolve(performer, null)
            ),
            new ContextResolver[]{
                container.get(PlayerContextResolver.class),
                container.get(AccountContextResolver.class),
                container.get(DebugContextResolver.class),
                container.get(ServerContextResolver.class),
                container.get(SelfContextResolver.class),
            }
        );
    }

    @Test
    void parseEmpty() {
        assertThrows(CommandException.class, () -> parser.parse(user, "  "), "Empty command");
    }

    @Test
    void parseSimple() throws AdminException {
        CommandParser.Arguments arguments = parser.parse(user, "simple");

        assertEquals("simple", arguments.command());
        assertEquals("simple", arguments.line());
        assertEquals("", arguments.contextPath());
        assertEquals(Arrays.asList("simple"), arguments.arguments());
        assertInstanceOf(AggregationContext.class, arguments.context());
    }

    @Test
    void parseWithArguments() throws AdminException {
        CommandParser.Arguments arguments = parser.parse(user, "  cmd arg1   arg2  val1  ");

        assertEquals("cmd", arguments.command());
        assertEquals("cmd arg1   arg2  val1", arguments.line());
        assertEquals("", arguments.contextPath());
        assertEquals(Arrays.asList("cmd", "arg1", "arg2", "val1"), arguments.arguments());
        assertInstanceOf(AggregationContext.class, arguments.context());
    }

    @Test
    void parseWithForceSelfContext() throws AdminException {
        CommandParser.Arguments arguments = parser.parse(user, "!cmd arg");

        assertEquals("cmd", arguments.command());
        assertEquals("!cmd arg", arguments.line());
        assertEquals("!", arguments.contextPath());
        assertEquals(Arrays.asList("cmd", "arg"), arguments.arguments());
        assertSame(user.self(), arguments.context());
    }

    @Test
    void parseWithSubContext() throws AdminException {
        CommandParser.Arguments arguments = parser.parse(user, "> account cmd arg");

        assertEquals("cmd", arguments.command());
        assertEquals("> account cmd arg", arguments.line());
        assertEquals("> account", arguments.contextPath());
        assertEquals(Arrays.asList("cmd", "arg"), arguments.arguments());
        assertSame(user.self().child("account"), arguments.context());
    }

    @Test
    void parseWithNotFoundSubContext() {
        assertThrows(ContextNotFoundException.class, () -> parser.parse(user, ">notFound cmd"));
    }

    @Test
    void parseWithResolvedContext() throws ContainerException, AdminException {
        Player player = dataSet.pushPlayer("John", 5, 2);

        GameSession session = (GameSession) container.get(SessionFactory.class).create(new DummyChannel());

        session.attach(new GameAccount(
            new Account(5),
            container.get(AccountService.class),
            2
        ));

        GamePlayer john = container.get(PlayerService.class).load(
            session,
            player.id()
        );

        CommandParser.Arguments arguments = parser.parse(user, "@John cmd arg");
        assertEquals("cmd", arguments.command());
        assertEquals("@John cmd arg", arguments.line());
        assertEquals("@John", arguments.contextPath());
        assertEquals(Arrays.asList("cmd", "arg"), arguments.arguments());
        assertInstanceOf(PlayerContext.class, arguments.context());
        assertSame(john, PlayerContext.class.cast(arguments.context()).player());
    }

    @Test
    void parseWithResolvedContextWithoutArgument() throws ContainerException, AdminException {
        session.attach(new GameAccount(
            new Account(5),
            container.get(AccountService.class),
            2
        ));

        CommandParser.Arguments arguments = parser.parse(user, ":cmd arg");
        assertEquals("cmd", arguments.command());
        assertEquals(":cmd arg", arguments.line());
        assertEquals(":", arguments.contextPath());
        assertEquals(Arrays.asList("cmd", "arg"), arguments.arguments());
        assertInstanceOf(DebugContext.class, arguments.context());
    }

    @Test
    void parseWithBadContextArgument() {
        assertThrows(ContextException.class, () -> parser.parse(user, "@NotFound cmd"), "Cannot found the player NotFound");
    }
}
