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
import fr.quatrevieux.araknemu.game.admin.help.CommandHelp;
import inet.ipaddr.IPAddressString;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionHandlerRegistry;
import org.kohsuke.args4j.ParserProperties;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.SubCommandHandler;

import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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
    public <@Nullable A> A hydrate(Command<A> command, @Nullable A commandArguments, CommandParser.Arguments parsedArguments) throws Exception {
        if (commandArguments == null) {
            throw new IllegalArgumentException("A command arguments instance must be provided");
        }

        final CmdLineParser parser = new CmdLineParser(commandArguments, parserProperties);

        // @todo handle custom properties
        parser.parseArgument(parsedArguments.arguments().subList(1, parsedArguments.arguments().size()));

        return commandArguments;
    }

    @Override
    @SuppressWarnings("argument") // args4j do not use nullable annotations
    public <A> CommandHelp help(Command<A> command, @Nullable A commandArguments, CommandHelp help) {
        final HelpGenerator generator = new HelpGenerator(command, new CmdLineParser(commandArguments, parserProperties));

        return help.modify(builder -> {
            generator.arguments(builder);
            generator.options(builder);
            generator.synopsis(builder);
        });
    }

    @Override
    public <A> boolean supports(Command<A> command, @Nullable A commandArguments) {
        return commandArguments != null;
    }

    private static class HelpGenerator {
        private final Command command;
        private final CmdLineParser parser;
        private final Map<String, String> defaultOptionValues = new HashMap<>();

        public HelpGenerator(Command command, CmdLineParser parser) {
            this.command = command;
            this.parser = parser;
        }

        public void synopsis(CommandHelp.Builder help) {
            if (!help.hasSynopsis()) {
                help.synopsis(parseSynopsis());
            }
        }

        public void options(CommandHelp.Builder help) {
            parseOptions(parser.getOptions(), option -> option.option.toString(), help);
        }

        @SuppressWarnings("argument") // library do not use Nullable annotation
        public void arguments(CommandHelp.Builder help) {
            parseOptions(parser.getArguments(), argument -> argument.getNameAndMeta(null), help);
        }

        private void parseOptions(List<OptionHandler> options, Function<OptionHandler, String> nameMapper, CommandHelp.Builder help) {
            for (OptionHandler option : options) {
                final String name = nameMapper.apply(option);

                if (!help.hasOption(name) && !option.option.usage().isEmpty()) {
                    help.option(name, option.option.usage());
                }

                if (!option.option.required()) {
                    final String defaultValue = option.printDefaultValue();

                    if (!isEmptyDefault(defaultValue)) {
                        defaultOptionValues.put(name, defaultValue);
                    }
                }
            }
        }

        private boolean isEmptyDefault(String defaultValue) {
            return defaultValue == null || defaultValue.isEmpty()
                || "false".equals(defaultValue) || "0".equals(defaultValue)
            ;
        }

        private String parseSynopsis() {
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            parser.printSingleLineUsage(os);

            String synopsis = command.name() + os;

            for (Map.Entry<String, String> mapping : defaultOptionValues.entrySet()) {
                synopsis = synopsis.replaceFirst("\\[(" + mapping.getKey() + ".*?)\\]", "[$1=" + mapping.getValue() + "]");
            }

            return synopsis;
        }
    }
}
