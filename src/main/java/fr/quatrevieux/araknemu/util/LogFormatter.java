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

package fr.quatrevieux.araknemu.util;

/**
 * Handle simple log formatting, like SLF4J / Log4j (See: http://www.slf4j.org/api/org/slf4j/helpers/MessageFormatter.html)
 * The message can be formatted using "{}" placeholder.
 */
public final class LogFormatter {
    private static final String PLACEHOLDER = "{}";

    private LogFormatter() {}

    /**
     * Format the given message
     *
     * Ex:
     * LogFormatter.format("Hello World !") => "Hello World !"
     * LogFormatter.format("Hello {} !", "John") => "Hello John !"
     * LogFormatter.format("Hello {}, my name is {} and I'm {} y-o", "John", "Alan", 36)
     *
     * @param message Message to format
     * @param parameters List of parameters
     *
     * @return The formatted message
     *
     * @throws IndexOutOfBoundsException When an invalid parameters count is given
     */
    public static String format(String message, Object... parameters) {
        final StringBuilder formatted = new StringBuilder(message.length());

        int lastPlaceholder = 0;
        int parameterNumber = 0;

        for (int placeholderIndex; (placeholderIndex = message.indexOf(PLACEHOLDER, lastPlaceholder)) != -1;) {
            if (parameterNumber >= parameters.length) {
                throw new IndexOutOfBoundsException("Missing parameter " + parameterNumber);
            }

            formatted
                .append(message, lastPlaceholder, placeholderIndex)
                .append(parameters[parameterNumber++])
            ;

            lastPlaceholder = placeholderIndex + PLACEHOLDER.length();
        }

        formatted.append(message.substring(lastPlaceholder));

        return formatted.toString();
    }
}
