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

import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.Command;
import fr.quatrevieux.araknemu.game.admin.CommandParser;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Handle {@code List<String>} argument type
 * The list will be filled with parsed arguments, skipping the first one (which contains the command name)
 */
public final class StringListHydrator implements ArgumentsHydrator {
    @Override
    @SuppressWarnings("unchecked")
    public <A> A hydrate(Command<A> command, A commandArguments, CommandParser.Arguments parsedArguments) {
        final List<String> inputArguments = parsedArguments.arguments().subList(1, parsedArguments.arguments().size());

        if (commandArguments == null) {
            return (A) inputArguments;
        }

        final List<String> arguments = (List<String>) commandArguments;

        arguments.addAll(parsedArguments.arguments().subList(1, parsedArguments.arguments().size()));

        return (A) arguments;
    }

    @Override
    public <A> boolean supports(Command<A> command, A commandArguments) {
        if (commandArguments != null && !(commandArguments instanceof List)) {
            return false;
        }

        try {
            // Get declared type of the createArguments method : this is the only available method for retrieve the generic type
            final Type argumentsType = command.getClass().getMethod("createArguments").getGenericReturnType();

            if (checkType(argumentsType)) {
                return true;
            }

            // Same as AbstractTypedArgumentsHydrator, but with generic type
            return checkType(command.getClass().getMethod("execute", AdminPerformer.class, List.class).getGenericParameterTypes()[1]);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkType(Type argumentsType) {
        if (!(argumentsType instanceof ParameterizedType)) {
            return false;
        }

        final ParameterizedType parameterizedType = (ParameterizedType) argumentsType;

        return
            parameterizedType.getRawType().equals(List.class)
            && parameterizedType.getActualTypeArguments()[0].equals(String.class)
        ;
    }
}
