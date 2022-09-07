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
import fr.quatrevieux.araknemu.game.admin.exception.CommandException;
import fr.quatrevieux.araknemu.game.admin.help.CommandHelp;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Aggregation of hydrators
 *
 * Check if inner hydrator supports the given arguments, hydrate it and return
 * If there is not supported hydrator, an {@link CommandException} will be thrown
 */
public final class HydratorsAggregate implements ArgumentsHydrator {
    private final List<ArgumentsHydrator> hydrators = new ArrayList<>();

    public HydratorsAggregate(ArgumentsHydrator[] hydrators) {
        for (ArgumentsHydrator hydrator : hydrators) {
            register(hydrator);
        }
    }

    public HydratorsAggregate() {
        this(new ArgumentsHydrator[] {
            new VoidHydrator(),
            new StringListHydrator(),
            new ParsedArgumentsHydrator(),
            new StringHydrator(),
            new AnnotationHydrator(),
        });
    }

    public void register(ArgumentsHydrator hydrator) {
        hydrators.add(hydrator);
    }

    @Override
    public <@Nullable A> A hydrate(Command<A> command, @Nullable A commandArguments, CommandParser.Arguments parsedArguments) throws Exception {
        return supportedHydrator(command, commandArguments)
            .orElseThrow(() -> new CommandException(command.name(), "Cannot parse arguments for command " + command.getClass().getSimpleName()))
            .hydrate(command, commandArguments, parsedArguments)
        ;
    }

    @Override
    public <A> CommandHelp help(Command<A> command, A commandArguments, CommandHelp help) {
        return supportedHydrator(command, commandArguments)
            .map(hydrator -> hydrator.help(command, commandArguments, help))
            .orElse(help)
        ;
    }

    @Override
    public <A> boolean supports(Command<A> command, @Nullable A commandArguments) {
        return supportedHydrator(command, commandArguments).isPresent();
    }

    private <A> Optional<ArgumentsHydrator> supportedHydrator(Command<A> command, @Nullable A commandArguments) {
        for (ArgumentsHydrator hydrator : hydrators) {
            if (hydrator.supports(command, commandArguments)) {
                return Optional.of(hydrator);
            }
        }

        return Optional.empty();
    }
}
