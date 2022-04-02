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

package fr.quatrevieux.araknemu.game.admin.exception;

import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.CommandParser;
import fr.quatrevieux.araknemu.game.admin.exception.handler.CommandExceptionHandler;
import fr.quatrevieux.araknemu.game.admin.exception.handler.CommandNotFoundExceptionHandler;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Handle exceptions
 */
public final class ExceptionHandler {
    private final Map<Class, Function> handlers = new HashMap<>();
    private final Function defaultHandler;

    public ExceptionHandler() {
        defaultHandler = (user, e, arguments) -> user.error("Error : {}", e.toString());

        register(CommandNotFoundException.class, new CommandNotFoundExceptionHandler());
        register(CommandException.class, new CommandExceptionHandler());
        register(CommandPermissionsException.class, (user, e, arguments) -> user.error("Unauthorized command '{}', you need at least these permissions {}", e.command(), e.permissions()));
        register(ContextException.class, (user, e, arguments) -> user.error("Error during resolving context : {}", e.getMessage() != null ? e.getMessage() : e.toString()));
        register(ContextNotFoundException.class, (user, e, arguments) -> user.error("The context '{}' is not found", e.context()));
    }

    /**
     * Handle the error
     */
    public void handle(AdminPerformer performer, Throwable error) {
        final CommandParser.Arguments arguments;

        Function handler;
        Throwable baseError = error;

        // The exception is wrapped into a CommandExecutionException
        // So we can get the parsed arguments
        if (error instanceof CommandExecutionException) {
            final CommandExecutionException ex = (CommandExecutionException) error;
            arguments = ex.arguments();
            baseError = ex.getCause();
        } else {
            arguments = null;
        }

        handler = handlers.get(baseError.getClass());

        // Use CommandExecutionException as CommandException
        if (handler == null && error instanceof CommandException) {
            handler = handlers.get(CommandException.class);
            baseError = error;
        }

        if (handler == null) {
            handler = defaultHandler;
        }

        handler.handle(performer, baseError, arguments);
    }

    /**
     * Register a new exception handler
     *
     * @param type The exception class (note: it should be the exact exception type)
     * @param handler The exception handler
     *
     * @param <T> The exception type
     */
    public <T extends Throwable> void register(Class<T> type, Function<T> handler) {
        handlers.put(type, handler);
    }

    @FunctionalInterface
    public interface Function<T extends Throwable> {
        /**
         * Handle the exception
         *
         * @param performer The command performer
         * @param error The triggered error
         * @param arguments The execution arguments. Can be null
         */
        public void handle(AdminPerformer performer, T error, CommandParser.@Nullable Arguments arguments);
    }
}
