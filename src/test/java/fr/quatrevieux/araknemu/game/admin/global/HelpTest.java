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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.admin.global;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.game.admin.AdminUser;
import fr.quatrevieux.araknemu.game.admin.CommandParser;
import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.executor.CommandExecutor;
import fr.quatrevieux.araknemu.game.admin.executor.argument.ArgumentsHydrator;
import fr.quatrevieux.araknemu.network.game.out.basic.admin.CommandResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HelpTest extends CommandTestCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        command = new Help(container.get(ArgumentsHydrator.class));
    }

    @Test
    void executeGlobal() throws SQLException, AdminException {
        execute("help");

        assertSuccess("<b>Admin console usage</b>");
        assertSuccess("<b>List of available commands :</b>");

        assertInfo("<u><a href='asfunction:onHref,ExecCmd,help setlife,true'>setlife</a></u> - Change the player current life");
        assertInfo("<u><a href='asfunction:onHref,ExecCmd,help help,true'>help</a></u> - Show help for use the console commands");
        assertInfo("<u><a href='asfunction:onHref,ExecCmd,help info,true'>info</a></u> - Display information on the selected player");
    }

    @Test
    void executeShouldFilterForbiddenCommands() throws SQLException, AdminException {
        final AdminUser performer = user();
        final CommandParser.Arguments parsedArgs = new CommandParser.Arguments("", "", command.name(), Collections.singletonList("help"), user().self());

        performer.account().get().grant(Permission.ACCESS);

        requestStack.clear();
        container.get(CommandExecutor.class).execute(command, performer, parsedArgs);
        List<String> messages = requestStack.channel.getMessages().stream().filter(CommandResult.class::isInstance).skip(6).map(Object::toString).map(s -> s.substring(4)).collect(Collectors.toList());

        assertTrue(messages.stream().anyMatch(s -> s.contains("msg")));
        assertTrue(messages.stream().anyMatch(s -> s.contains("help")));
        assertFalse(messages.stream().anyMatch(s -> s.contains("kick")));
        assertFalse(messages.stream().anyMatch(s -> s.contains("goto")));
        assertFalse(messages.stream().anyMatch(s -> s.contains("ban")));
        assertFalse(messages.stream().anyMatch(s -> s.contains("grant")));

        performer.account().get().grant(Permission.values());

        requestStack.clear();

        container.get(CommandExecutor.class).execute(command, performer, parsedArgs);
        messages = requestStack.channel.getMessages().stream().filter(CommandResult.class::isInstance).skip(6).map(Object::toString).map(s -> s.substring(4)).collect(Collectors.toList());

        assertTrue(messages.stream().anyMatch(s -> s.contains("msg")));
        assertTrue(messages.stream().anyMatch(s -> s.contains("help")));
        assertTrue(messages.stream().anyMatch(s -> s.contains("kick")));
        assertTrue(messages.stream().anyMatch(s -> s.contains("goto")));
        assertTrue(messages.stream().anyMatch(s -> s.contains("ban")));
        assertTrue(messages.stream().anyMatch(s -> s.contains("grant")));
    }

    @Test
    void executeGlobalWithContext() throws SQLException, AdminException {
        executeLine("> account help");

        assertSuccess("<b>Admin console usage</b>");
        assertSuccess("<b>List of available commands :</b>");

        assertInfo("<u><a href='asfunction:onHref,ExecCmd,> account help help,true'>help</a></u> - Show help for use the console commands");
        assertInfo("<u><a href='asfunction:onHref,ExecCmd,> account help info,true'>info</a></u> - Display info about the account");
    }

    @Test
    void executeCommand() throws SQLException, AdminException {
        execute("help", "setlife");

        assertSuccess("<b>Help for setlife</b>");
        assertInfo(
            "setlife - Change the player current life\n" +
            "========================================\n" +
            "\n" +
            "<b>SYNOPSIS</b>\n" +
            "\tsetlife number|max\n\n" +
            "<b>EXAMPLES</b>\n" +
            "\t<u><a href='asfunction:onHref,ExecCmd,setlife 300,false'>setlife 300</a></u>       - Set the current player life to 300\n" +
            "\t<u><a href='asfunction:onHref,ExecCmd,setlife max,false'>setlife max</a></u>       - Set full life to the current player\n" +
            "\t<u><a href='asfunction:onHref,ExecCmd,@John setlife 250,false'>@John setlife 250</a></u> - Set John's life to 250\n\n" +
            "<b>PERMISSIONS</b>\n" +
            "\t[ACCESS, MANAGE_PLAYER]"
        );
    }

    @Test
    void help() {
        assertHelp(
            "help - Show help for use the console commands",
            "========================================",
            "SYNOPSIS",
                "\thelp [COMMAND NAME]",
            "EXAMPLES",
                "\thelp      - List all available commands",
                "\thelp echo - Show the help for the echo command",
            "PERMISSIONS",
                "\t[ACCESS]"
        );
    }
}
