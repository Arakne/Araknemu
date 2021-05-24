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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.admin;

import fr.quatrevieux.araknemu.game.admin.context.Context;
import fr.quatrevieux.araknemu.game.admin.context.ContextResolver;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.exception.CommandException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextNotFoundException;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Command line parser using context system
 *
 * Examples:
 *
 * Simple command :
 * echo Hello World !
 *
 * Apply on self :
 * !getitem 456 2
 *
 * Child context :
 * >account ban 120s
 *
 * Resolve context :
 * @Robert levelup 55
 *
 * Resolve child context :
 * @Robert > account gift 45 3
 */
public final class ContextCommandParser implements CommandParser {
    private final Map<Character, ContextResolver> resolvers = new HashMap<>();

    public ContextCommandParser(ContextResolver... resolvers) {
        for (ContextResolver resolver : resolvers) {
            this.resolvers.put(resolver.prefix(), resolver);
        }
    }

    @Override
    public Arguments parse(AdminPerformer performer, String line) throws AdminException {
        final State state = new State(performer, line.trim());

        if (state.line.isEmpty()) {
            throw new CommandException("Empty command");
        }

        final Context context = parseContext(state);
        final String contextPath = state.before().trim();
        final String command = parseCommand(state);
        final List<String> arguments = parseArguments(state);

        return new Arguments(
            state.line,
            contextPath,
            command,
            arguments,
            context
        );
    }

    /**
     * Parse command arguments
     *
     * The first argument is the command name, and arguments are separated with white space
     */
    private List<String> parseArguments(State state) {
        return Arrays.asList(
            StringUtils.split(state.after(), " ")
        );
    }

    /**
     * Parse the command name
     *
     * The command name is the first argument of the command line
     */
    private String parseCommand(State state) {
        return StringUtils.substringBefore(state.after(), " ");
    }

    /**
     * Parse the command context
     *
     * If the line starts with !, the context is the current admin user
     * If the line stats with $, the context is dynamic
     *
     * After resolve the root context, resolve the child contexts separated by >
     */
    private Context parseContext(State state) throws AdminException {
        final char prefix = state.current();
        final Context context;

        if (resolvers.containsKey(prefix)) {
            state.next();
            context = resolvers.get(prefix).resolve(state.performer, () -> state.skipBlank().nextWord());
        } else {
            context = state.performer.self(); // @todo séparer contexte "self" et "par défaut"
        }

        return resolveChildContext(state, context);
    }

    /**
     * Resolve the child context
     * This method will be called recursively until no more child context is detected
     *
     * Child contexts are separated by >
     */
    private Context resolveChildContext(State state, Context context) throws ContextNotFoundException {
        state.skipBlank();

        if (state.current() != '>') {
            return context;
        }

        final String name = state.next().skipBlank().nextWord();

        return resolveChildContext(state, context.child(name));
    }

    private static class State {
        private final AdminPerformer performer;
        private final String line;
        private int cursor = 0;

        public State(AdminPerformer performer, String line) {
            this.performer = performer;
            this.line = line;
        }

        /**
         * Get the character at the cursor position
         */
        public char current() {
            return line.charAt(cursor);
        }

        /**
         * Move to the next character
         */
        public State next() {
            ++cursor;

            return this;
        }

        /**
         * Check if there is more characters on the line
         */
        public boolean hasNext() {
            return cursor < line.length();
        }

        /**
         * Move to the next word (i.e. when a non letter or digit character is encountered) and return the current word
         *
         * @return The extracted word
         */
        public String nextWord() {
            return moveWhile(Character::isLetterOrDigit);
        }

        /**
         * Move the cursor while the predicate is valid for the current character
         *
         * @param predicate The character tester
         *
         * @return The "move" part, starting at the current cursor position (included), and ending with the last valid position (excluded)
         */
        public String moveWhile(Predicate<Character> predicate) {
            final int position = cursor;

            while (hasNext() && predicate.test(current())) {
                next();
            }

            return line.substring(position, cursor);
        }

        /**
         * Skip white space characters
         */
        public State skipBlank() {
            while (hasNext() && Character.isSpaceChar(current())) {
                next();
            }

            return this;
        }

        /**
         * Get the line part after the cursor (included)
         */
        public String after() {
            return line.substring(cursor);
        }

        /**
         * Get the line part before the cursor (excluded)
         */
        public String before() {
            return line.substring(0, cursor);
        }
    }
}
