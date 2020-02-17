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
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.exception.CommandException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextNotFoundException;
import fr.quatrevieux.araknemu.game.admin.player.PlayerContext;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.network.game.GameSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @todo recursive context child
 * @todo context resolver without argument
 */
class AdminUserCommandParserTest extends GameBaseCase {
    private AdminUserCommandParser parser;
    private AdminUser user;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        parser = new AdminUserCommandParser(
            user = container.get(AdminService.class).user(gamePlayer())
        );
    }

    @Test
    void parseEmpty() {
        assertThrows(CommandException.class, () -> parser.parse("  "), "Empty command");
    }

    @Test
    void parseSimple() throws AdminException {
        CommandParser.Arguments arguments = parser.parse("simple");

        assertEquals("simple", arguments.command());
        assertEquals("simple", arguments.line());
        assertEquals("", arguments.contextPath());
        assertEquals(Arrays.asList("simple"), arguments.arguments());
        assertSame(user.context().current(), arguments.context());
    }

    @Test
    void parseWithArguments() throws AdminException {
        CommandParser.Arguments arguments = parser.parse("  cmd arg1   arg2  val1  ");

        assertEquals("cmd", arguments.command());
        assertEquals("cmd arg1   arg2  val1", arguments.line());
        assertEquals("", arguments.contextPath());
        assertEquals(Arrays.asList("cmd", "arg1", "arg2", "val1"), arguments.arguments());
        assertSame(user.context().current(), arguments.context());
    }

    @Test
    void parseWithForceSelfContext() throws AdminException {
        user.context().setCurrent(new NullContext());

        CommandParser.Arguments arguments = parser.parse("!cmd arg");

        assertEquals("cmd", arguments.command());
        assertEquals("!cmd arg", arguments.line());
        assertEquals("!", arguments.contextPath());
        assertEquals(Arrays.asList("cmd", "arg"), arguments.arguments());
        assertSame(user.context().self(), arguments.context());
    }

    @Test
    void parseWithNamedContext() throws AdminException {
        Context context = new NullContext();
        user.context().set("myContext", context);

        CommandParser.Arguments arguments = parser.parse("$myContext cmd arg");

        assertEquals("cmd", arguments.command());
        assertEquals("$myContext cmd arg", arguments.line());
        assertEquals("$myContext", arguments.contextPath());
        assertEquals(Arrays.asList("cmd", "arg"), arguments.arguments());
        assertSame(context, arguments.context());
    }

    @Test
    void parseWithNotFoundNamedContext() {
        assertThrows(ContextNotFoundException.class, () -> parser.parse("$notFound cmd"));
    }

    @Test
    void parseWithSubContext() throws AdminException {
        CommandParser.Arguments arguments = parser.parse("> account cmd arg");

        assertEquals("cmd", arguments.command());
        assertEquals("> account cmd arg", arguments.line());
        assertEquals("> account", arguments.contextPath());
        assertEquals(Arrays.asList("cmd", "arg"), arguments.arguments());
        assertSame(user.context().self().child("account"), arguments.context());
    }

    @Test
    void parseWithNotFoundSubContext() {
        assertThrows(ContextNotFoundException.class, () -> parser.parse(">notFound cmd"));
    }

    @Test
    void parseWithAnonymousContext() throws ContainerException, AdminException {
        Player player = dataSet.pushPlayer("John", 5, 2);

        GameSession session = (GameSession) container.get(SessionFactory.class).create(new DummyChannel());

        session.attach(new GameAccount(
            new Account(5),
            container.get(AccountService.class),
            2
        ));

        container.get(PlayerService.class).load(
            session,
            player.id()
        );

        CommandParser.Arguments arguments = parser.parse("${player:John} cmd arg");
        assertEquals("cmd", arguments.command());
        assertEquals("${player:John} cmd arg", arguments.line());
        assertEquals("${player:John}", arguments.contextPath());
        assertEquals(Arrays.asList("cmd", "arg"), arguments.arguments());
        assertInstanceOf(PlayerContext.class, arguments.context());
    }

    @Test
    void parseWithBadContextType() {
        assertThrows(ContextException.class, () -> parser.parse("${badType:Arg} cmd"), "Context type 'badType' not found");
    }

    @Test
    void parseWithBadContextArgument() {
        assertThrows(ContextException.class, () -> parser.parse("${player:NotFound} cmd"), "Cannot found the player NotFound");
    }

    @Test
    void parseWithBadContextSyntax() {
        assertThrows(CommandException.class, () -> parser.parse("${player:"), "Syntax error : missing closing accolade");
    }
}
