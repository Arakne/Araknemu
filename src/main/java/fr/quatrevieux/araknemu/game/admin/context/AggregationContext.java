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

package fr.quatrevieux.araknemu.game.admin.context;

import fr.quatrevieux.araknemu.game.admin.Command;
import fr.quatrevieux.araknemu.game.admin.exception.CommandNotFoundException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextNotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Handle aggregation of contexts
 *
 * The firsts contexts has the priority compared to the following on command or context name conflict
 */
public final class AggregationContext implements Context {
    private final Context[] contexts;

    public AggregationContext(Context... contexts) {
        this.contexts = contexts;
    }

    @Override
    public Command command(String name) throws CommandNotFoundException {
        for (Context context : contexts) {
            try {
                return context.command(name);
            } catch (CommandNotFoundException e) {
                // Ignore
            }
        }

        throw new CommandNotFoundException(name);
    }

    @Override
    public Collection<Command> commands() {
        final Map<String, Command> commands = new HashMap<>();

        for (Context context : contexts) {
            context.commands().forEach(command -> commands.putIfAbsent(command.name(), command));
        }

        return commands.values();
    }

    @Override
    public Context child(String name) throws ContextNotFoundException {
        for (Context context : contexts) {
            try {
                return context.child(name);
            } catch (ContextNotFoundException e) {
                // Ignore
            }
        }

        throw new ContextNotFoundException(name);
    }
}
