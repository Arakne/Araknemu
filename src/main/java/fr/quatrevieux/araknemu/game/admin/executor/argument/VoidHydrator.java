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

/**
 * Handle commands without arguments, which use {@link Void} as argument type
 */
public final class VoidHydrator implements ArgumentsHydrator {
    @Override
    public <A> A hydrate(Command<A> command, A commandArguments, CommandParser.Arguments parsedArguments) throws Exception {
        return null;
    }

    @Override
    public <A> boolean supports(Command<A> command, A commandArguments) {
        if (commandArguments != null) {
            return false;
        }

        try {
            command.getClass().getMethod("execute", AdminPerformer.class, Void.class);

            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}
