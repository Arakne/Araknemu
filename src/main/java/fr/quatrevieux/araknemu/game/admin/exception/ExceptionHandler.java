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

import java.util.HashMap;
import java.util.Map;

/**
 * Handle exceptions
 */
public final class ExceptionHandler {
    private final Map<Class, Function> handlers = new HashMap<>();

    public ExceptionHandler() {
        register(CommandNotFoundException.class, (user, e) -> user.error("Command '{}' is not found", e.command()));
        register(CommandException.class, (user, e) -> user.error("An error occurs during execution of '{}' : {}", e.command(), e.getMessage()));
        register(CommandPermissionsException.class, (user, e) -> user.error("Unauthorized command '{}', you need at least these permissions {}", e.command(), e.permissions()));
        register(ContextException.class, (user, e) -> user.error("Error during resolving context : {}", e.getMessage()));
        register(ContextNotFoundException.class, (user, e) -> user.error("The context '{}' is not found", e.context()));
    }

    /**
     * Handle the error
     */
    public void handle(AdminPerformer performer, Throwable error) {
        if (!handlers.containsKey(error.getClass())) {
            performer.error("Error : {}", error.toString());
            return;
        }

        handlers.get(error.getClass()).handle(performer, error);
    }

    private <T extends Throwable> void register(Class<T> type, Function<T> handler) {
        handlers.put(type, handler);
    }

    @FunctionalInterface
    public interface Function<T extends Throwable> {
        /**
         * Handle the exception
         *
         * @param performer The command performer
         * @param error The triggered error
         */
        public void handle(AdminPerformer performer, T error);
    }
}
