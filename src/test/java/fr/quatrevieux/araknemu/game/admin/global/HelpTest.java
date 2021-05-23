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

import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.executor.argument.ArgumentsHydrator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

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
            "\t<u><a href='asfunction:onHref,ExecCmd,setlife 300,false'>setlife 300</a></u>                - Set the current player life to 300\n" +
            "\t<u><a href='asfunction:onHref,ExecCmd,setlife max,false'>setlife max</a></u>                - Set full life to the current player\n" +
            "\t<u><a href='asfunction:onHref,ExecCmd,${player:John} setlife 250,false'>${player:John} setlife 250</a></u> - Set John's life to 250\n\n" +
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
