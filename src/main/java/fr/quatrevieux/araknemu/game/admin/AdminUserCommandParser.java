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

import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.exception.CommandException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextNotFoundException;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Admin user parser for command line
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
 * Named context :
 * $global info
 *
 * Anonymous context :
 * ${player:Robert} levelup 55
 *
 * Child anonymous context :
 * ${player:Robert} > account gift 45 3
 */
final public class AdminUserCommandParser implements CommandParser {
    final private AdminUser user;

    static private class State {
        final private String line;
        private int cursor = 0;

        public State(String line) {
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
            int position = cursor;

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

    public AdminUserCommandParser(AdminUser user) {
        this.user = user;
    }

    @Override
    public Arguments parse(String line) throws AdminException {
        State state = new State(line.trim());

        if (state.line.isEmpty()) {
            throw new CommandException("Empty command");
        }

        Context context = parseContext(state);
        String contextPath = state.before().trim();
        String command = parseCommand(state);
        List<String> arguments = parseArguments(state);

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
        Context context;

        switch (state.current()) {
            case '!':
                state.next();
                context = user.context().self();
                break;

            case '$':
                state.next();
                context = resolveDynamicContext(state);
                break;

            default:
                context = user.context().current();
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

        String name = state.next().skipBlank().nextWord();

        return resolveChildContext(state, context.child(name));
    }

    /**
     * Resolve a dynamic context (start with $)
     *
     * If the line starts with {, try to resolve an anonymous context
     * Else, get an already registered context
     */
    private Context resolveDynamicContext(State state) throws AdminException {
        if (state.current() == '{') {
            return resolveAnonymousContext(state.next());
        }

        return user.context().get(state.nextWord());
    }

    /**
     * Resolve the anonymous context between accolades
     *
     * The anonymous context is in form : ${type:argument}
     */
    private Context resolveAnonymousContext(State state) throws AdminException {
        String[] arguments = StringUtils.split(state.moveWhile(c -> c != '}'), ":", 2);

        if (!state.hasNext()) {
            throw new CommandException("Syntax error : missing closing accolade");
        }

        state
            .next()
            .skipBlank()
        ;

        return user.context().resolve(
            arguments[0],
            arguments.length == 1 ? "" : arguments[1]
        );
    }
}
