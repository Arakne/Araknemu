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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.admin.script;

import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import fr.quatrevieux.araknemu.game.admin.context.NullContext;
import fr.quatrevieux.araknemu.game.admin.debug.DebugContext;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.exception.CommandNotFoundException;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class ScriptLoaderContextConfiguratorTest extends CommandTestCase {
    @Test
    void loadSimpleCommand() throws AdminException, SQLException {
        Logger logger = Mockito.mock(Logger.class);
        DebugContext context = new DebugContext(
            new NullContext(),
            Arrays.asList(
                new ScriptLoaderContextConfigurator<>(Paths.get("src/test/scripts/commands/simple"), c -> container, logger)
            )
        );

        context.commands(); // Load context

        Mockito.verify(logger).debug(Mockito.eq("Load command script {}"), Mockito.endsWith("SimpleCommand.groovy"));
        Mockito.verify(logger).debug(Mockito.eq("Find command {}"), Mockito.eq("SimpleCommand"));

        command = context.command("simple");
        execute("simple", "foo");

        assertOutput("Execute command simple with argument foo");
        assertHelp(
            "simple - My description",
            "========================================",
            "SYNOPSIS",
                "\tsimple ARG",
            "PERMISSIONS",
                "\t[ACCESS]"
        );
    }

    @Test
    void loadCommandWithDependencies() throws AdminException, SQLException, NoSuchFieldException, IllegalAccessException {
        Logger logger = Mockito.mock(Logger.class);
        DebugContext context = new DebugContext(
            new NullContext(),
            Arrays.asList(
                new ScriptLoaderContextConfigurator<>(Paths.get("src/test/scripts/commands/with_dep"), c -> container, logger)
            )
        );

        context.commands(); // Load context

        command = context.command("withdep");

        assertSame(container.get(ExplorationMapService.class), command.getClass().getField("mapService").get(command));
        assertSame(container.get(PlayerService.class), command.getClass().getField("playerService").get(command));
    }

    @Test
    void hydrators() throws AdminException, SQLException {
        Logger logger = Mockito.mock(Logger.class);
        DebugContext context = new DebugContext(
            new NullContext(),
            Arrays.asList(
                new ScriptLoaderContextConfigurator<>(Paths.get("src/test/scripts/commands/hydrators"), c -> container, logger)
            )
        );

        command = context.command("simplestr");
        execute("simplestr", "foo");
        assertOutput("arguments : foo");

        command = context.command("strlist");
        execute("strlist", "foo", "bar", "baz");
        assertOutput("arguments : [foo, bar, baz]");

        command = context.command("parsed");
        execute("parsed", "foo", "bar", "baz");
        assertOutput("arguments : command = parsed, arguments = [parsed, foo, bar, baz], context = ");

        command = context.command("custom");
        execute("custom", "--opt", "bar");
        assertOutput("arguments : Arguments{arg=bar, opt=true}");
    }

    @Test
    void invalidFileShouldLogAsError() {
        Logger logger = Mockito.mock(Logger.class);
        DebugContext context = new DebugContext(
            new NullContext(),
            Arrays.asList(
                new ScriptLoaderContextConfigurator<>(Paths.get("src/test/scripts/commands/invalid"), c -> container, logger)
            )
        );

        context.commands(); // load commands

        Mockito.verify(logger).debug(Mockito.eq("Load command script {}"), Mockito.endsWith("InvalidCommand.groovy"));
        Mockito.verify(logger).error(Mockito.eq("Fail to load command script"), Mockito.<Exception>any());
    }

    @Test
    void directoryNotFoundShouldSkip() {
        Logger logger = Mockito.mock(Logger.class);
        DebugContext context = new DebugContext(
            new NullContext(),
            Arrays.asList(
                new ScriptLoaderContextConfigurator<>(Paths.get("src/test/scripts/commands/not_found"), c -> container, logger)
            )
        );

        context.commands(); // load commands

        Mockito.verify(logger, Mockito.never()).debug(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void loadShouldKeepCompiledClassAfterReload() throws AdminException, SQLException {
        Logger logger = Mockito.mock(Logger.class);
        ScriptLoaderContextConfigurator<DebugContext> loader = new ScriptLoaderContextConfigurator<>(Paths.get("src/test/scripts/commands/simple"), c -> container, logger);

        DebugContext context = new DebugContext(new NullContext(), Arrays.asList(loader));

        Class commandClass = context.command("simple").getClass();

        DebugContext newContext = new DebugContext(new NullContext(), Arrays.asList(loader));

        assertSame(commandClass, newContext.command("simple").getClass());
    }

    @Test
    void loadDirectoryCreatedAfterStartup() throws IOException, CommandNotFoundException {
        Path directory = Paths.get("src/test/scripts/commands/not_exists");

        if (Files.isDirectory(directory)) {
            Files.walk(directory)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                    }
                })
            ;
        }

        Logger logger = Mockito.mock(Logger.class);
        ScriptLoaderContextConfigurator<DebugContext> loader = new ScriptLoaderContextConfigurator<>(directory, c -> container, logger);

        DebugContext context = new DebugContext(new NullContext(), Arrays.asList(loader));
        assertTrue(context.commands().isEmpty());

        Files.createDirectory(directory);
        Files.copy(Paths.get("src/test/scripts/commands/simple/SimpleCommand.groovy"), directory.resolve("SimpleCommand.groovy"));

        context = new DebugContext(new NullContext(), Arrays.asList(loader));
        assertEquals("SimpleCommand", context.command("simple").getClass().getSimpleName());
        Files.walk(directory)
            .sorted(Comparator.reverseOrder())
            .forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {}
            })
        ;
    }
}
