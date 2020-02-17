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

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AbstractCommandTest extends GameBaseCase {
    @Test
    void defaults() {
        Command command = new AbstractCommand() {
            @Override
            protected void build(Builder builder) {}

            @Override
            public String name() {
                return "cmd";
            }

            @Override
            public void execute(AdminPerformer output, List<String> arguments) {}
        };

        assertEquals("No description", command.description());
        assertEquals(
            "cmd - No description\n" +
            "========================================\n\n" +
            "<b>SYNOPSIS</b>\n" +
            "\tcmd\n\n" +
            "<b>PERMISSIONS</b>\n" +
            "\t[ACCESS]",
            command.help()
        );
        assertEquals(EnumSet.of(Permission.ACCESS), command.permissions());
    }

    @Test
    void withDescriptionAndHelp() {
        Command command = new AbstractCommand() {
            @Override
            protected void build(Builder builder) {
                builder
                    .description("My very useful command")
                    .help(formatter -> formatter.line("Do what you wants"))
                    .requires(Permission.SUPER_ADMIN)
                ;
            }

            @Override
            public String name() {
                return "cmd";
            }

            @Override
            public void execute(AdminPerformer output, List<String> arguments) {}
        };

        assertEquals("My very useful command", command.description());
        assertEquals(
            "cmd - My very useful command\n" +
            "========================================\n\n" +
            "<b>SYNOPSIS</b>\n" +
            "\tcmd\n" +
            "Do what you wants" +
            "\n\n<b>PERMISSIONS</b>\n" +
            "\t[ACCESS, SUPER_ADMIN]",
            command.help()
        );
        assertEquals(EnumSet.of(Permission.ACCESS, Permission.SUPER_ADMIN), command.permissions());
    }
}
