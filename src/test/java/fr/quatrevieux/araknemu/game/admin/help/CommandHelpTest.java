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

package fr.quatrevieux.araknemu.game.admin.help;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.Command;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.formatter.Link;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandHelpTest {
    private Command command = new Command<Void>() {
        @Override
        public String name() {
            return "cmd";
        }

        @Override
        public CommandHelp help() {
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
            "cmd - No description\n" +
            "========================================\n\n" +
            "<b>SYNOPSIS</b>\n" +
            "\tcmd\n\n" +
            "<b>PERMISSIONS</b>\n" +
            "\t[ACCESS]",
            new CommandHelp(command).toString()
        );
    }

    @Test
    void synopsis() {
        assertEquals(
            "cmd - No description\n" +
            "========================================\n\n" +
            "<b>SYNOPSIS</b>\n" +
            "\tcmd [options]\n\n" +
            "<b>PERMISSIONS</b>\n" +
            "\t[ACCESS]",
            new CommandHelp(command).modify(builder -> builder.synopsis("cmd [options]")).toString()
        );
    }

    @Test
    void description() {
        assertEquals(
            "cmd - My cmd description\n" +
            "========================================\n\n" +
            "<b>SYNOPSIS</b>\n" +
            "\tcmd\n\n" +
            "<b>PERMISSIONS</b>\n" +
            "\t[ACCESS]",
            new CommandHelp(command).modify(builder -> builder.description("My cmd description")).toString()
        );
    }

    @Test
    void option() {
        assertEquals(
            "cmd - No description\n" +
            "========================================\n\n" +
            "<b>SYNOPSIS</b>\n" +
            "\tcmd\n\n" +
            "<b>OPTIONS</b>\n" +
            "\t--opt : my option\n" +
            "\t\ton multiple lines\n" +
            "\t--other : other option\n\n" +
            "<b>PERMISSIONS</b>\n" +
            "\t[ACCESS]",
            new CommandHelp(command).modify(builder -> builder
                .option("--opt", "my option\non multiple lines")
                .option("--other", "other option")
            ).toString()
        );
    }

    @Test
    void example() {
        assertEquals(
            "cmd - No description\n" +
            "========================================\n\n" +
            "<b>SYNOPSIS</b>\n" +
            "\tcmd\n\n" +
            "<b>EXAMPLES</b>\n" +
            "\t<u><a href='asfunction:onHref,ExecCmd,cmd arg,false'>cmd arg</a></u>       - first example\n" +
            "\t<u><a href='asfunction:onHref,ExecCmd,cmd other arg,false'>cmd other arg</a></u> - second example\n\n" +
            "<b>PERMISSIONS</b>\n" +
            "\t[ACCESS]",
            new CommandHelp(command).modify(builder -> builder
                .example("cmd arg", "first example")
                .example("cmd other arg", "second example")
            ).toString()
        );
    }

    @Test
    void exampleTooLongShouldBeAligned() {
        assertEquals(
            "cmd - No description\n" +
            "========================================\n\n" +
            "<b>SYNOPSIS</b>\n" +
            "\tcmd\n\n" +
            "<b>EXAMPLES</b>\n" +
            "\t<u><a href='asfunction:onHref,ExecCmd,my cmd with too long arguments list,false'>my cmd with too long arguments list</a></u> - first example\n" +
            "\t<u><a href='asfunction:onHref,ExecCmd,cmd other,false'>cmd other</a></u> - second example\n\n" +
            "<b>PERMISSIONS</b>\n" +
            "\t[ACCESS]",
            new CommandHelp(command).modify(b -> b
                .example("my cmd with too long arguments list", "first example")
                .example("cmd other", "second example")
            ).toString()
        );
    }

    @Test
    void seeAlso() {
        assertEquals(
            "cmd - No description\n" +
                "========================================\n\n" +
                "<b>SYNOPSIS</b>\n" +
                "\tcmd\n\n" +
                "<b>SEE ALSO</b>\n" +
                "\t<u><a href='asfunction:onHref,ExecCmd,help cmd,true'>cmd</a></u>   - first\n" +
                "\t<u><a href='asfunction:onHref,ExecCmd,other,true'>other</a></u> - second\n\n" +
                "<b>PERMISSIONS</b>\n" +
                "\t[ACCESS]",
            new CommandHelp(command).modify(b -> b
                .seeAlso("cmd", "first")
                .seeAlso("other", "second", Link.Type.EXECUTE)
            ).toString()
        );
    }

    @Test
    void line() {
        assertEquals(
            "cmd - No description\n" +
                "========================================\n\n" +
                "<b>SYNOPSIS</b>\n" +
                "\tcmd\n" +
                "first\n" +
                "second\n\n" +
                "<b>PERMISSIONS</b>\n" +
                "\t[ACCESS]",
            new CommandHelp(command).modify(b -> b
                .line("first", "second")
            ).toString()
        );
    }

    @Test
    void hasSynopsis() {
        new CommandHelp(command).modify(b ->
            assertTrue(b.synopsis("cmd ARG").hasSynopsis())
        );

        new CommandHelp(command).modify(b ->
            assertFalse(b.hasSynopsis())
        );
    }

    @Test
    void hasOption() {
        new CommandHelp(command).modify(b -> {
                b.option("opt", "descr opt");

                assertTrue(b.hasOption("opt"));
                assertFalse(b.hasOption("foo"));
        }).toString();
    }

    @Test
    void modifyShouldNoModifyCurrentInstance() {
        CommandHelp help = new CommandHelp(command);
        CommandHelp modified = help.modify(builder -> builder.synopsis("cmd [options]").description("My description"));

        assertNotSame(help, modified);

        assertEquals(
            "cmd - No description\n" +
                "========================================\n\n" +
                "<b>SYNOPSIS</b>\n" +
                "\tcmd\n\n" +
                "<b>PERMISSIONS</b>\n" +
                "\t[ACCESS]",
            help.toString()
        );

        assertEquals(
            "cmd - My description\n" +
                "========================================\n\n" +
                "<b>SYNOPSIS</b>\n" +
                "\tcmd [options]\n\n" +
                "<b>PERMISSIONS</b>\n" +
                "\t[ACCESS]",
            modified.toString()
        );
    }

    @Test
    void withSimpleVariable() {
        assertEquals(
            "cmd - My complex command description with My variable\n" +
            "========================================\n" +
            "\n" +
            "<b>SYNOPSIS</b>\n" +
                "\tcmd ARG\n" +
            "\n" +
            "<b>OPTIONS</b>\n" +
                "\tARG : Should use My variable\n" +
            "\n" +
            "<b>PERMISSIONS</b>\n" +
                "\t[ACCESS]",
            new CommandHelp(command).modify(builder -> {
                builder
                    .description("My complex command description with {{var}}")
                    .synopsis("cmd ARG")
                    .option("ARG", "Should use {{var}}")
                    .with("var", () -> "My variable")
                ;
            }).toString()
        );
    }

    @Test
    void withStringVariable() {
        assertEquals(
            "cmd - Use constant : Constant value\n" +
            "========================================\n" +
            "\n" +
            "<b>SYNOPSIS</b>\n" +
                "\tcmd\n" +
            "\n" +
            "<b>PERMISSIONS</b>\n" +
                "\t[ACCESS]",
            new CommandHelp(command).modify(builder -> {
                builder
                    .description("Use constant : {{var}}")
                    .with("var", "Constant value")
                ;
            }).toString()
        );
    }

    enum MyEnum {
        FOO, BAR
    }

    @Test
    void withEnumVariable() {
        assertEquals(
            "cmd - Use enum : FOO, BAR\n" +
            "========================================\n" +
            "\n" +
            "<b>SYNOPSIS</b>\n" +
                "\tcmd\n" +
            "\n" +
            "<b>PERMISSIONS</b>\n" +
                "\t[ACCESS]",
            new CommandHelp(command).modify(builder -> {
                builder
                    .description("Use enum : {{enum}}")
                    .with("enum", MyEnum.class)
                ;
            }).toString()
        );
    }
}
