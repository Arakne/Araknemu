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

package fr.quatrevieux.araknemu.game.admin.formatter;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.Command;
import fr.quatrevieux.araknemu.game.admin.CommandParser;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class HelpFormatterTest {
    private Command command = new Command<Void>() {
        @Override
        public String name() {
            return "cmd";
        }

        @Override
        public String description() {
            return "cmd description";
        }

        @Override
        public HelpFormatter help() {
            return null;
        }

        @Override
        public void execute(AdminPerformer performer, Void arguments) throws AdminException {}

        @Override
        public Set<Permission> permissions() {
            return EnumSet.of(Permission.ACCESS);
        }
    };

    @Test
    void empty() {
        assertEquals(
            "cmd - cmd description\n" +
            "========================================\n\n" +
            "<b>SYNOPSIS</b>\n" +
            "\tcmd\n\n" +
            "<b>PERMISSIONS</b>\n" +
            "\t[ACCESS]",
            new HelpFormatter(command).toString()
        );
    }

    @Test
    void synopsis() {
        assertEquals(
            "cmd - cmd description\n" +
            "========================================\n\n" +
            "<b>SYNOPSIS</b>\n" +
            "\tcmd [options]\n\n" +
            "<b>PERMISSIONS</b>\n" +
            "\t[ACCESS]",
            new HelpFormatter(command).synopsis("cmd [options]").toString()
        );
    }

    @Test
    void options() {
        assertEquals(
            "cmd - cmd description\n" +
            "========================================\n\n" +
            "<b>SYNOPSIS</b>\n" +
            "\tcmd\n\n" +
            "<b>OPTIONS</b>\n" +
            "\t--opt : my option\n" +
            "\t\ton multiple lines\n" +
            "\t--other : other option\n\n" +
            "<b>PERMISSIONS</b>\n" +
            "\t[ACCESS]",
            new HelpFormatter(command)
                .options("--opt", "my option\non multiple lines")
                .options("--other", "other option")
                .toString()
        );
    }

    @Test
    void example() {
        assertEquals(
            "cmd - cmd description\n" +
            "========================================\n\n" +
            "<b>SYNOPSIS</b>\n" +
            "\tcmd\n\n" +
            "<b>EXAMPLES</b>\n" +
            "\t<u><a href='asfunction:onHref,ExecCmd,cmd arg,false'>cmd arg</a></u>       - first example\n" +
            "\t<u><a href='asfunction:onHref,ExecCmd,cmd other arg,false'>cmd other arg</a></u> - second example\n\n" +
            "<b>PERMISSIONS</b>\n" +
            "\t[ACCESS]",
            new HelpFormatter(command)
                .example("cmd arg", "first example")
                .example("cmd other arg", "second example")
                .toString()
        );
    }

    @Test
    void exampleTooLongShouldBeAligned() {
        assertEquals(
            "cmd - cmd description\n" +
            "========================================\n\n" +
            "<b>SYNOPSIS</b>\n" +
            "\tcmd\n\n" +
            "<b>EXAMPLES</b>\n" +
            "\t<u><a href='asfunction:onHref,ExecCmd,my cmd with too long arguments list,false'>my cmd with too long arguments list</a></u> - first example\n" +
            "\t<u><a href='asfunction:onHref,ExecCmd,cmd other,false'>cmd other</a></u> - second example\n\n" +
            "<b>PERMISSIONS</b>\n" +
            "\t[ACCESS]",
            new HelpFormatter(command)
                .example("my cmd with too long arguments list", "first example")
                .example("cmd other", "second example")
                .toString()
        );
    }

    @Test
    void seeAlso() {
        assertEquals(
            "cmd - cmd description\n" +
                "========================================\n\n" +
                "<b>SYNOPSIS</b>\n" +
                "\tcmd\n\n" +
                "<b>SEE ALSO</b>\n" +
                "\t<u><a href='asfunction:onHref,ExecCmd,help cmd,true'>cmd</a></u>   - first\n" +
                "\t<u><a href='asfunction:onHref,ExecCmd,other,true'>other</a></u> - second\n\n" +
                "<b>PERMISSIONS</b>\n" +
                "\t[ACCESS]",
            new HelpFormatter(command)
                .seeAlso("cmd", "first")
                .seeAlso("other", "second", Link.Type.EXECUTE)
                .toString()
        );
    }

    @Test
    void line() {
        assertEquals(
            "cmd - cmd description\n" +
                "========================================\n\n" +
                "<b>SYNOPSIS</b>\n" +
                "\tcmd\n" +
                "first\n" +
                "second\n\n" +
                "<b>PERMISSIONS</b>\n" +
                "\t[ACCESS]",
            new HelpFormatter(command)
                .line("first", "second")
                .toString()
        );
    }

    @Test
    void defaultSynopsis() {
        assertEquals(
            "cmd - cmd description\n" +
                "========================================\n\n" +
                "<b>SYNOPSIS</b>\n" +
                "\tcmd ARG\n\n" +
                "<b>PERMISSIONS</b>\n" +
                "\t[ACCESS]",
            new HelpFormatter(command)
                .synopsis("cmd ARG")
                .defaultSynopsis("other")
                .toString()
        );

        assertEquals(
            "cmd - cmd description\n" +
                "========================================\n\n" +
                "<b>SYNOPSIS</b>\n" +
                "\tother\n\n" +
                "<b>PERMISSIONS</b>\n" +
                "\t[ACCESS]",
            new HelpFormatter(command)
                .defaultSynopsis("other")
                .toString()
        );
    }

    @Test
    void defaultOption() {
        assertEquals(
            "cmd - cmd description\n" +
            "========================================\n" +
            "\n" +
            "<b>SYNOPSIS</b>\n" +
            "\tcmd\n" +
            "\n" +
            "<b>OPTIONS</b>\n" +
            "\topt : descr opt\n" +
            "\tfoo : bar\n" +
            "\n" +
            "<b>PERMISSIONS</b>\n" +
            "\t[ACCESS]",
            new HelpFormatter(command)
                .options("opt", "descr opt")
                .defaultOption("opt", "other descr")
                .defaultOption("foo", "bar")
                .toString()
        );
    }
}
