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
import fr.quatrevieux.araknemu.game.admin.executor.argument.handler.IpAddressStringHandler;
import fr.quatrevieux.araknemu.game.admin.executor.argument.handler.LocalTimeHandler;
import fr.quatrevieux.araknemu.game.admin.executor.argument.type.SubArguments;
import fr.quatrevieux.araknemu.game.admin.formatter.HelpFormatter;
import inet.ipaddr.IPAddressString;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionHandlerRegistry;
import org.kohsuke.args4j.ParserProperties;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.SubCommandHandler;

import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Hydrator using {@link org.kohsuke.args4j.CmdLineParser} parser for fill arguments
 * This is the default hydrator
 */
public final class AnnotationHydrator implements ArgumentsHydrator {
    private final ParserProperties parserProperties = ParserProperties.defaults().withAtSyntax(false);

    public AnnotationHydrator() {
        OptionHandlerRegistry.getRegistry().registerHandler(Duration.class, DurationOptionHandler::new);
        OptionHandlerRegistry.getRegistry().registerHandler(IPAddressString.class, IpAddressStringHandler::new);
        OptionHandlerRegistry.getRegistry().registerHandler(LocalTime.class, LocalTimeHandler::new);
        OptionHandlerRegistry.getRegistry().registerHandler(SubArguments.class, SubCommandHandler::new);
    }

    @Override
    public <A> A hydrate(Command<A> command, A commandArguments, CommandParser.Arguments parsedArguments) throws Exception {
        final CmdLineParser parser = new CmdLineParser(commandArguments, parserProperties);

        // @todo handle custom properties
        parser.parseArgument(parsedArguments.arguments().subList(1, parsedArguments.arguments().size()));

        return commandArguments;
    }

    @Override
    public <A> HelpFormatter help(Command<A> command, A commandArguments, HelpFormatter help) {
        final CmdLineParser parser = new CmdLineParser(commandArguments, parserProperties);
        final Map<String, String> defaultOptionValues = new HashMap<>();

        for (OptionHandler argument : parser.getArguments()) {
            final String argumentName = argument.getNameAndMeta(null);

            if (!argument.option.usage().isEmpty()) {
                help.defaultOption(argumentName, argument.option.usage());
            }

            if (!argument.option.required()) {
                final String defaultValue = argument.printDefaultValue();

                if (!isEmptyDefault(defaultValue)) {
                    defaultOptionValues.put(argumentName, defaultValue);
                }
            }
        }

        for (OptionHandler option : parser.getOptions()) {
            if (!option.option.usage().isEmpty()) {
                help.defaultOption(option.option.toString(), option.option.usage());
            }

            if (!option.option.required()) {
                final String defaultValue = option.printDefaultValue();

                if (!isEmptyDefault(defaultValue)) {
                    defaultOptionValues.put(option.option.toString(), defaultValue);
                }
            }
        }

        help.defaultSynopsis(generateSynopsis(command, parser, defaultOptionValues));

        // @todo handle default value on synopsis ?

        return help;
    }

    @Override
    public <A> boolean supports(Command<A> command, A commandArguments) {
        return commandArguments != null;
    }

    private String generateSynopsis(Command command, CmdLineParser parser, Map<String, String> defaultsMap) {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        parser.printSingleLineUsage(os);

        // @todo check
        String synopsis = command.name() + os;

        for (Map.Entry<String, String> mapping : defaultsMap.entrySet()) {
            synopsis = synopsis.replaceFirst("\\[(" + mapping.getKey() + ".*?)\\]", "[$1=" + mapping.getValue() + "]");
        }

        return synopsis;
    }

    private boolean isEmptyDefault(String defaultValue) {
        return defaultValue == null || defaultValue.isEmpty()
            || "false".equals(defaultValue) || "0".equals(defaultValue)
        ;
    }
}
