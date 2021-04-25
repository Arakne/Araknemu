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

package fr.quatrevieux.araknemu.game.admin.executor.argument;

import fr.quatrevieux.araknemu.game.admin.Command;
import fr.quatrevieux.araknemu.game.admin.CommandParser;
import fr.quatrevieux.araknemu.game.admin.executor.argument.handler.DurationOptionHandler;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionHandlerRegistry;
import org.kohsuke.args4j.ParserProperties;

import java.time.Duration;

/**
 * Hydrator using {@link org.kohsuke.args4j.CmdLineParser} parser for fill arguments
 * This is the default hydrator
 */
public final class AnnotationHydrator implements ArgumentsHydrator {
    private final ParserProperties parserProperties = ParserProperties.defaults().withAtSyntax(false);

    public AnnotationHydrator() {
        OptionHandlerRegistry.getRegistry().registerHandler(Duration.class, DurationOptionHandler::new);
    }

    @Override
    public <A> A hydrate(Command<A> command, A commandArguments, CommandParser.Arguments parsedArguments) throws Exception {
        final CmdLineParser parser = new CmdLineParser(commandArguments, parserProperties);

        // @todo handle custom properties
        parser.parseArgument(parsedArguments.arguments().subList(1, parsedArguments.arguments().size()));

        return commandArguments;
    }

    @Override
    public <A> boolean supports(Command<A> command, A commandArguments) {
        return commandArguments != null;
    }
}
