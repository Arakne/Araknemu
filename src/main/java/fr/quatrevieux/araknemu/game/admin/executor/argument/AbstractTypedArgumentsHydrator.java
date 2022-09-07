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
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Simple ArgumentsHydrator using declared command argument for the supports method
 *
 * @param <T> The supported type
 */
public abstract class AbstractTypedArgumentsHydrator<T> implements ArgumentsHydrator {
    @Override
    @SuppressWarnings("unchecked")
    public final <@Nullable A> A hydrate(Command<A> command, @Nullable A commandArguments, CommandParser.Arguments parsedArguments) throws Exception {
        return (A) typedHydrate((Command<T>) command, (@Nullable T) commandArguments, parsedArguments);
    }

    /**
     * Implementation of the hydrate method with correct type
     */
    protected abstract T typedHydrate(Command<T> command, @Nullable T commandArguments, CommandParser.Arguments parsedArguments) throws Exception;

    @Override
    public final <A> boolean supports(Command<A> command, @Nullable A commandArguments) {
        final Class<T> supported = type();

        if (supported.isInstance(commandArguments)) {
            return true;
        }

        try {
            final Class<?> declaredReturnType = command.getClass().getMethod("createArguments").getReturnType();

            // The argument type is explicitly declared
            if (supported.equals(declaredReturnType)) {
                return true;
            }

            // Not the default type : the command do not supports the required type
            if (!declaredReturnType.equals(Object.class)) {
                return false;
            }

            // Check if the execute method takes a the required type as parameter
            // Use this hack because execute() is the only method ensured to be overridden
            // So we can check the real parameter here
            command.getClass().getMethod("execute", AdminPerformer.class, supported);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @return The supported type class
     */
    protected abstract Class<T> type();
}
